package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.tax.TaxEditDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
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
    private final ExpenseService expenseService;

    public TaxService(TaxRepository taxRepository, ModelMapper modelMapper, BuildingService buildingService, ApartmentService apartmentService,
                      ExpenseRepository expenseRepository, ExpenseService expenseService) {
        this.taxRepository = taxRepository;
        this.modelMapper = modelMapper;
        this.buildingService = buildingService;
        this.apartmentService = apartmentService;
        this.expenseRepository = expenseRepository;
        this.expenseService = expenseService;
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

    private static boolean taxIsPaid(TaxEntity taxToUpdate) {
        return taxToUpdate.getTaxStatus().equals(TaxStatusEnum.PAID);
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

    public Page<TaxEntity> findAllTaxesByBuildingIdPaginated(Pageable pageable, Long buildingId) {
        List<TaxEntity> taxes = taxRepository
                .findAllTaxesByBuildingOrderByDueDateAscAndTaxStatus(buildingId);
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

        return taxRepository.findTopFiveApartmentsByDebtInBuilding(PageRequest.of(0,5),buildingId);
    }
}
