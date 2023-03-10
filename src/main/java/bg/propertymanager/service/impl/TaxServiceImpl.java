package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.tax.TaxPayDTO;
import bg.propertymanager.model.dto.tax.TaxReturnDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import bg.propertymanager.repository.TaxRepository;
import bg.propertymanager.service.ApartmentService;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.ExpenseService;
import bg.propertymanager.service.TaxService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TaxServiceImpl implements TaxService {
    private final TaxRepository taxRepository;
    private final ModelMapper modelMapper;
    private final BuildingService buildingService;
    private final ApartmentService apartmentService;
    private final ExpenseService expenseService;

    public TaxServiceImpl(TaxRepository taxRepository, ModelMapper modelMapper, @Lazy BuildingService buildingService, ApartmentService apartmentService,
                         @Lazy ExpenseService expenseService) {
        this.taxRepository = taxRepository;
        this.modelMapper = modelMapper;
        this.buildingService = buildingService;
        this.apartmentService = apartmentService;
        this.expenseService = expenseService;
    }

    @Override
    public TaxViewDTO findViewById(Long taxId) {
        return taxRepository.findById(taxId)
                .map(tax -> modelMapper.map(tax, TaxViewDTO.class))
                .orElseThrow(() -> new NullPointerException("No tax is existing with id " + taxId));
    }

    @Override
    public BigDecimal calculateBuildingBalance(Long buildingId) {
        BigDecimal paidTaxes = taxRepository
                .findAmountOfPaidTaxesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
        BigDecimal paidExpenses = expenseService
                .findAmountOfAllPaidExpensesByBuildingId(buildingId);

        return paidTaxes.subtract(paidExpenses);
    }

    @Override
    public BigDecimal findOwedMoney(Long apartmentId) {
        return taxRepository
                .findOwedMoneyByApartmentId(apartmentId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void save(TaxEntity newTaxToAdd) {
        taxRepository.save(newTaxToAdd);
    }

    @Override
    public Set<TaxEntity> addTaxForEachApartment(List<String> apartments, BuildingEntity buildingToUpdate,
                                                 ExpenseEntity expense) {
        BigDecimal taxForEachApartment = getTaxForEachApartment(expense, apartments.size());
        Set<TaxEntity> expenseTaxes = new HashSet<>();
        for (String apartmentNumber : apartments) {
            ApartmentEntity apartmentToAddTax = apartmentService.getApartmentByApartmentNumberAndBuildingId(apartmentNumber, buildingToUpdate.getId());

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

    @Override
    public Boolean findIfPaidTaxesExistToExpenseById(Long expenseId) {
        return taxRepository.existsByTaxStatusPaidAndExpenseId(expenseId);
    }

    @Override
    public void deleteAllTaxesAttachedToExpense(ExpenseEntity expenseToDelete) {
        for (TaxEntity tax : expenseToDelete.getTaxes()) {
            apartmentService.deleteTaxFromApartment(tax);
            buildingService.deleteTaxFromBuilding(tax);
            taxRepository.delete(tax);
        }
    }

    @Override
    public void setApartmentToNullWhenDeleting(ApartmentEntity apartmentToRemove) {
        Set<TaxEntity> taxesOfApartment = taxRepository
                .findAllByApartment_Id(apartmentToRemove.getId());
        for (TaxEntity tax : taxesOfApartment) {
            tax.setApartment(null);
            taxRepository.save(tax);
        }
    }

    @Override
    public BigDecimal findAmountOfAllPaidTaxesByBuildingId(Long buildingId) {
        return taxRepository.findAmountOfPaidTaxesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal findAmountOfAllUnpaidTaxesByBuildingId(Long buildingId) {
        return taxRepository.findAmountOfUnpaidTaxesByBuildingId(buildingId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Page<TaxEntity> findAllTaxesByBuildingIdFilteredAndPaginated(Pageable pageable, Long buildingId, String searchKeyword) {
        List<TaxEntity> taxes = getTaxesByBuildingWithOrWithoutFilter(searchKeyword, buildingId);
        return getPageOfTaxes(pageable, taxes);
    }

    @Override
    public Page<TaxEntity> findAllTaxesByBuildingIdAndOwnerId(Pageable pageable, Long buildingId, Long neighbourId) {
        List<TaxEntity> taxes = taxRepository
                .findAllTaxesByBuildingIdAndOwnerId(buildingId, neighbourId);
        return getPageOfTaxes(pageable, taxes);
    }

    @Override
    public Page<ApartmentEntity> findTopFiveApartmentsInBuildingByDebt(Long buildingId) {

        return taxRepository.findTopFiveApartmentsByDebtInBuilding(PageRequest.of(0, 5), buildingId);
    }

    @Override
    public Page<TaxEntity> findALlMyTaxesByBuildingIdPaginated(Pageable pageable, Long buildingId, String ownerUsername) {
        List<TaxEntity> taxes = taxRepository
                .findAllTaxesByBuildingIdAndOwnerUsername(buildingId, ownerUsername);
        return getPageOfTaxes(pageable, taxes);
    }

    @Override
    public void payTaxAmount(TaxPayDTO taxPayDTO) {
        TaxEntity taxToBePaid = getTaxById(taxPayDTO.getId());
        payTaxAmountAndChangeStatusIfNeeded(taxPayDTO, taxToBePaid);
        taxRepository.save(taxToBePaid);
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

    @Override
    public void returnTaxAmount(TaxReturnDTO taxReturnDTO) {
        TaxEntity taxToReturnMoneyFrom = getTaxById(taxReturnDTO.getId());
        returnTaxAmountAndChangeStatusIfNeeded(taxReturnDTO, taxToReturnMoneyFrom);
        taxRepository.save(taxToReturnMoneyFrom);
    }

    @Override
    public Boolean checkIfUserIsOwnerOfTax(String ownerUsername, Long taxId) {
        TaxEntity tax = getTaxById(taxId);
        return tax.getApartment().getOwner().getUsername().equals(ownerUsername);
    }

    @Override
    public boolean checkIfPaidAmountIsMoreThanRemainingAmount(Long taxId, BigDecimal paidAmount) {
        TaxEntity tax = getTaxById(taxId);
        return paidAmount.compareTo(tax.getAmount().subtract(tax.getPaidAmount())) > 0;
    }

    @Override
    public boolean checkIfReturnedMoneyIsMoreThanPaidMoney(Long taxId, BigDecimal returnedAmount) {
        TaxEntity tax = getTaxById(taxId);
        return returnedAmount.compareTo(tax.getPaidAmount()) > 0;
    }

    @Override
    public void createPeriodicTaxesForEveryApartment() {
        List<ApartmentEntity> apartments = apartmentService.findAllApartmentsWithPositivePeriodicTax();
        LocalDateTime now = LocalDateTime.now();
        for (ApartmentEntity apartment : apartments) {
            BuildingEntity currentBuilding = apartment.getBuilding();
            TaxEntity newPeriodicTaxToAdd = new TaxEntity()
                    .setTaxType(TaxTypeEnum.PERIODIC)
                    .setAmount(apartment.getPeriodicTax())
                    .setPaidAmount(BigDecimal.ZERO)
                    .setTaxStatus(TaxStatusEnum.UNPAID)
                    .setDescription("Periodic tax for " + now.getMonth().name())
                    .setStartDate(now)
                    .setDueDate(now.plusMonths(1))
                    .setBuilding(currentBuilding)
                    .setManager(currentBuilding.getManager())
                    .setApartment(apartment)
                    .setExpense(null);
            buildingService.addNewTaxToBuilding(currentBuilding, newPeriodicTaxToAdd);
            apartmentService.addNewTaxToApartment(apartment, newPeriodicTaxToAdd);
            taxRepository.save(newPeriodicTaxToAdd);
        }
    }

    private void returnTaxAmountAndChangeStatusIfNeeded(TaxReturnDTO taxReturnDTO, TaxEntity taxToReturnMoneyFrom) {
        if (checkIfAlreadyPaidAmountIsMoreThanReturnedAmount(taxToReturnMoneyFrom.getPaidAmount(), taxReturnDTO.getReturnedAmount())) {
            taxToReturnMoneyFrom.setTaxStatus(TaxStatusEnum.PARTLY_PAID);
        } else {
            taxToReturnMoneyFrom.setTaxStatus(TaxStatusEnum.UNPAID);
        }
        taxToReturnMoneyFrom.setPaidAmount(taxToReturnMoneyFrom.getPaidAmount().subtract(taxReturnDTO.getReturnedAmount()));
    }

    private static PageImpl<TaxEntity> getPageOfTaxes(Pageable pageable, List<TaxEntity> taxes) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TaxEntity> list;
        list = getTaxEntities(taxes, pageSize, startItem);
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), taxes.size());
    }

    private static BigDecimal getTaxForEachApartment(ExpenseEntity expense, int apartmentsCount) {
        return expense.getAmount()
                .divide(BigDecimal.valueOf(apartmentsCount), RoundingMode.CEILING);
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

    private static void payTaxAmountAndChangeStatusIfNeeded(TaxPayDTO taxPayDTO, TaxEntity taxToBePaid) {
        if (checkIfAmountToBePaidIsMoreThanPaidAmount(taxPayDTO, taxToBePaid)) {
            taxToBePaid.setTaxStatus(TaxStatusEnum.PARTLY_PAID);
        } else {
            taxToBePaid.setTaxStatus(TaxStatusEnum.PAID);
        }
        taxToBePaid.setPaidAmount(taxToBePaid.getPaidAmount().add(taxPayDTO.getPaidAmount()));
    }

    private boolean checkIfAlreadyPaidAmountIsMoreThanReturnedAmount(BigDecimal paidAmount, BigDecimal returnedAmount) {
        return paidAmount.compareTo(returnedAmount) > 0;
    }

    private static boolean checkIfAmountToBePaidIsMoreThanPaidAmount(TaxPayDTO taxPayDTO, TaxEntity taxToBePaid) {
        return (taxToBePaid.getAmount().subtract(taxToBePaid.getPaidAmount())).compareTo(taxPayDTO.getPaidAmount()) > 0;
    }

    private TaxEntity getTaxById(Long taxId) {
        return taxRepository.findById(taxId)
                .orElseThrow(() -> new NullPointerException("There is no tax with this id " + taxId));
    }
}
