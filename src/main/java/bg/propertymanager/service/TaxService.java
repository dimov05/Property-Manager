package bg.propertymanager.service;

import bg.propertymanager.model.dto.tax.TaxPayDTO;
import bg.propertymanager.model.dto.tax.TaxReturnDTO;
import bg.propertymanager.model.dto.tax.TaxViewDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface TaxService {
    TaxViewDTO findViewById(Long taxId);

    BigDecimal calculateBuildingBalance(Long buildingId);

    BigDecimal findOwedMoney(Long apartmentId);

    void save(TaxEntity newTaxToAdd);

    Set<TaxEntity> addTaxForEachApartment(List<String> apartments, BuildingEntity buildingToUpdate,
                                          ExpenseEntity expense);

    Boolean findIfPaidTaxesExistToExpenseById(Long expenseId);

    void deleteAllTaxesAttachedToExpense(ExpenseEntity expenseToDelete);

    void setApartmentToNullWhenDeleting(ApartmentEntity apartmentToRemove);

    BigDecimal findAmountOfAllPaidTaxesByBuildingId(Long buildingId);

    BigDecimal findAmountOfAllUnpaidTaxesByBuildingId(Long buildingId);

    Page<TaxEntity> findAllTaxesByBuildingIdFilteredAndPaginated(Pageable pageable, Long buildingId, String searchKeyword);

    Page<TaxEntity> findAllTaxesByBuildingIdAndOwnerId(Pageable pageable, Long buildingId, Long neighbourId);

    Page<ApartmentEntity> findTopFiveApartmentsInBuildingByDebt(Long buildingId);

    Page<TaxEntity> findALlMyTaxesByBuildingIdPaginated(Pageable pageable, Long buildingId, String ownerUsername);

    void payTaxAmount(TaxPayDTO taxPayDTO);

    void returnTaxAmount(TaxReturnDTO taxReturnDTO);

    Boolean checkIfUserIsOwnerOfTax(String ownerUsername, Long taxId);

    boolean checkIfPaidAmountIsMoreThanRemainingAmount(Long taxId, BigDecimal paidAmount);

    boolean checkIfReturnedMoneyIsMoreThanPaidMoney(Long taxId, BigDecimal returnedAmount);

    void createPeriodicTaxesForEveryApartment();

}
