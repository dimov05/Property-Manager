package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.tax.TaxEditDTO;
import bg.propertymanager.model.dto.tax.TaxPayDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.view.UserEntityViewModel;
import bg.propertymanager.repository.ExpenseRepository;
import bg.propertymanager.repository.TaxRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaxService {
    private final TaxRepository taxRepository;
    private final ModelMapper modelMapper;
    private final BuildingService buildingService;
    private final ApartmentService apartmentService;
    private final ExpenseRepository expenseRepository;

    public TaxService(TaxRepository taxRepository, ModelMapper modelMapper, BuildingService buildingService, ApartmentService apartmentService,
                      ExpenseRepository expenseRepository) {
        this.taxRepository = taxRepository;
        this.modelMapper = modelMapper;
        this.buildingService = buildingService;
        this.apartmentService = apartmentService;
        this.expenseRepository = expenseRepository;
    }

    public List<TaxViewDTO> findAllTaxes(BuildingViewDTO building) {
        return taxRepository
                .findAllByBuilding_Id(building.getId())
                .stream()
                .map(tax -> modelMapper.map(tax, TaxViewDTO.class))
                .collect(Collectors.toList());

    }

    public TaxViewDTO findViewById(Long taxId) {
        return taxRepository.findById(taxId)
                .map(tax -> modelMapper.map(tax, TaxViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No tax is existing with id " + taxId));
    }

    public void updateTaxStatus(TaxEditDTO taxEditDTO) {
        TaxEntity taxToUpdate = taxRepository
                .findById(taxEditDTO.getId())
                .orElseThrow(() -> new NullPointerException("No tax is existing with id " + taxEditDTO.getId()));


        taxToUpdate
                .setTaxStatus(taxEditDTO.getTaxStatus());
        taxRepository.save(taxToUpdate);
    }

    public BigDecimal calculateBuildingBalance(Long buildingId) {
        BigDecimal paidTaxes = taxRepository
                .findAmountOfPaidTaxesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
        BigDecimal paidExpenses = expenseRepository
                .findAmountOfPaidExpensesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);

        return paidTaxes.subtract(paidExpenses);
    }

    public BigDecimal findOwedMoney(Long apartmentId) {
        return taxRepository
                .findOwedMoneyByApartmentId(apartmentId)
                .orElse(BigDecimal.ZERO);
    }

    public void save(TaxEntity newTaxToAdd) {
        taxRepository.save(newTaxToAdd);
    }

    public Set<TaxEntity> addTaxForEachApartment(List<String> apartments, BuildingEntity buildingToUpdate,
                                                 ExpenseEntity expense) {
        BigDecimal taxForEachApartment = getTaxForEachApartment(expense, apartments.size());
        Set<TaxEntity> expenseTaxes = new HashSet<>();
        for (String apartmentNumber : apartments) {
            ApartmentEntity apartmentToAddTax = apartmentService.getApartmentByApartmentNumber(apartmentNumber);

            TaxEntity newTaxToAdd = new TaxEntity()
                    .setTaxType(expense.getTaxType())
                    .setAmount(taxForEachApartment)
                    .setPaidAmount(BigDecimal.ZERO)
                    .setTaxStatus(TaxStatusEnum.UNPAID)
                    .setDescription(expense.getDescription())
                    .setStartDate(expense.getStartDate())
                    .setDueDate(expense.getDueDate())
                    .setBuilding(buildingToUpdate)
                    .setManager(buildingToUpdate.getManager())
                    .setApartment(apartmentToAddTax)
                    .setExpense(expense);
            buildingService.addNewTaxToBuilding(buildingToUpdate, newTaxToAdd);
            apartmentService.addNewTaxToApartment(apartmentToAddTax, newTaxToAdd);
            taxRepository.save(newTaxToAdd);
            expenseTaxes.add(newTaxToAdd);
        }
        return expenseTaxes;
    }

    private static BigDecimal getTaxForEachApartment(ExpenseEntity expense, int apartmentsCount) {
        return expense.getAmount()
                .divide(BigDecimal.valueOf(apartmentsCount), RoundingMode.HALF_UP);
    }

    public Boolean findIfPaidTaxesExistToExpenseById(Long expenseId) {
        return taxRepository.existsByTaxStatusPaidAndExpenseId(expenseId);
    }

    public void deleteAllTaxesAttachedToExpense(ExpenseEntity expenseToDelete) {
        for (TaxEntity tax : expenseToDelete.getTaxes()) {
            apartmentService.deleteTaxFromApartment(tax);
            buildingService.deleteTaxFromBuilding(tax);
            taxRepository.delete(tax);
        }
    }

    public void setApartmentToNullWhenDeleting(ApartmentEntity apartmentToRemove) {
        Set<TaxEntity> taxesOfApartment = taxRepository
                .findAllByApartment_Id(apartmentToRemove.getId());
        for (TaxEntity tax : taxesOfApartment) {
            tax.setApartment(null);
            taxRepository.save(tax);
        }
    }

    public BigDecimal findAmountOfAllPaidTaxesByBuildingId(Long buildingId) {
        return taxRepository.findAmountOfPaidTaxesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal findAmountOfAllUnpaidTaxesByBuildingId(Long buildingId) {
        return taxRepository.findAmountOfUnpaidTaxesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
    }

    public Page<TaxEntity> findAllTaxesByBuildingIdFilteredAndPaginated(Pageable pageable, Long buildingId, String searchKeyword) {
        List<TaxEntity> taxes = getTaxesByBuildingWithOrWithoutFilter(searchKeyword, buildingId);
        return getPageOfTaxes(pageable, taxes);
    }

    public Page<TaxEntity> findAllTaxesByBuildingIdAndOwnerId(Pageable pageable, Long buildingId, Long neighbourId) {
        List<TaxEntity> taxes = taxRepository
                .findAllTaxesByBuildingIdAndOwnerId(buildingId, neighbourId);
        return getPageOfTaxes(pageable, taxes);
    }

    private static PageImpl<TaxEntity> getPageOfTaxes(Pageable pageable, List<TaxEntity> taxes) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TaxEntity> list;
        list = getTaxEntities(taxes, pageSize, startItem);
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), taxes.size());
    }

    private static List<TaxEntity> getTaxEntities(List<TaxEntity> taxes, int pageSize, int startItem) {
        List<TaxEntity> list;
        if (taxes.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, taxes.size());
            list = taxes.subList(startItem, toIndex);
        }
        return list;
    }

    public Page<ApartmentEntity> findTopFiveApartmentsInBuildingByDebt(Long buildingId) {

        return taxRepository.findTopFiveApartmentsByDebtInBuilding(PageRequest.of(0, 5), buildingId);
    }

    private List<TaxEntity> getTaxesByBuildingWithOrWithoutFilter(String searchKeyword, Long buildingId) {
        List<TaxEntity> taxes;
        if (searchKeyword == null) {
            taxes = taxRepository
                    .findAllTaxesByBuildingOrderByDueDateAscAndTaxStatus(buildingId);
        } else {
            taxes = taxRepository
                    .findAllTaxesByBuildingIdFilteredByKeywordOrderByDueDateAscAndTaxStatus(buildingId, searchKeyword);
        }
        return taxes;
    }

    public Page<TaxEntity> findALlMyTaxesByBuildingIdPaginated(Pageable pageable, Long buildingId, String ownerUsername) {
        List<TaxEntity> taxes = taxRepository
                .findAllTaxesByBuildingIdAndOwnerUsername(buildingId, ownerUsername);
        return getPageOfTaxes(pageable, taxes);
    }

    public void payTaxAmount(TaxPayDTO taxPayDTO) {
        TaxEntity taxToBePaid = taxRepository
                .findById(taxPayDTO.getId())
                .orElseThrow(() -> new NullPointerException("There is no such a tax with this id " + taxPayDTO.getId()));

        payTaxAmountAndChangeStatusIfTaxIsPaid(taxPayDTO, taxToBePaid);
        taxRepository.save(taxToBePaid);
    }

    private static void payTaxAmountAndChangeStatusIfTaxIsPaid(TaxPayDTO taxPayDTO, TaxEntity taxToBePaid) {
        if (checkIfAmountToBePaidIsMoreThanPaidAmount(taxPayDTO, taxToBePaid)) {
            taxToBePaid.setTaxStatus(TaxStatusEnum.PARTLY_PAID);
        } else {
            taxToBePaid.setTaxStatus(TaxStatusEnum.PAID);
        }
        taxToBePaid.setPaidAmount(taxToBePaid.getPaidAmount().add(taxPayDTO.getPaidAmount()));
    }

    private static boolean checkIfAmountToBePaidIsMoreThanPaidAmount(TaxPayDTO taxPayDTO, TaxEntity taxToBePaid) {
        return (taxToBePaid.getAmount().subtract(taxToBePaid.getPaidAmount())).compareTo(taxPayDTO.getPaidAmount()) > 0;
    }

    public Boolean checkIfUserIsOwnerOfTax(String ownerUsername, Long taxId) {
        TaxEntity tax = taxRepository.findById(taxId)
                .orElseThrow(() -> new NullPointerException("There is no tax with this id " + taxId));
        return tax.getApartment().getOwner().getUsername().equals(ownerUsername);
    }

    public boolean checkIfPaidAmountIsMoreThanTax(Long taxId, BigDecimal paidAmount) {
        TaxEntity tax = taxRepository.findById(taxId)
                .orElseThrow(() -> new NullPointerException("There is no tax with this id " + taxId));
        return paidAmount.compareTo(tax.getAmount()) > 0;
    }
}
