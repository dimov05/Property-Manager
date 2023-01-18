package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.TaxEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TaxRepository extends JpaRepository<TaxEntity, Long> {
    Set<TaxEntity> findAllByBuilding_Id(Long buildingId);

    @Query("SELECT SUM(t.paidAmount)  FROM TaxEntity as t " +
            "WHERE t.building.id = :buildingId AND t.taxStatus = 'PAID' OR t.taxStatus = 'PARTLY_PAID'")
    Optional<BigDecimal> findAmountOfPaidTaxesByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT SUM(t.amount - t.paidAmount)  FROM TaxEntity as t " +
            "WHERE t.building.id = :buildingId AND t.taxStatus = 'UNPAID' OR t.taxStatus = 'PARTLY_PAID'")
    Optional<BigDecimal> findAmountOfUnpaidTaxesByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT SUM(t.amount-t.paidAmount) FROM TaxEntity  as t " +
            "WHERE t.apartment.id = :apartmentId AND t.taxStatus = 'UNPAID' OR t.taxStatus = 'PARTLY_PAID'")
    Optional<BigDecimal> findOwedMoneyByApartmentId(@Param("apartmentId") Long apartmentId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TaxEntity t " +
            "WHERE t.taxStatus = 'PAID' OR t.taxStatus = 'PARTLY_PAID' AND t.expense.id = :expenseId")
    Boolean existsByTaxStatusPaidAndExpenseId(@Param("expenseId") Long expenseId);

    @Query("SELECT t FROM TaxEntity as t WHERE t.building.id = :buildingId ORDER BY t.taxStatus desc, t.dueDate asc ")
    List<TaxEntity> findAllTaxesByBuildingOrderByDueDateAscAndTaxStatus(Long buildingId);

    @Query("select t FROM TaxEntity  as t where t.building.id = :buildingId AND t.apartment.owner.id = :ownerId " +
            "Order by t.taxStatus desc, t.dueDate asc")
    List<TaxEntity> findAllTaxesByBuildingIdAndOwnerId(Long buildingId, Long ownerId);

    Set<TaxEntity> findAllByApartment_Id(Long apartmentId);

    @Query("SELECT t.apartment, SUM(t.amount-t.paidAmount) FROM TaxEntity as t WHERE t.taxStatus = 'UNPAID' OR t.taxStatus = 'PARTLY_PAID' " +
            "GROUP BY t.apartment ORDER BY SUM(t.amount-t.paidAmount) desc")
    Page<ApartmentEntity> findTopFiveApartmentsByDebtInBuilding(Pageable pageable, Long buildingId);

    @Query("SELECT t FROM TaxEntity as t " +
            "WHERE t.building.id = :buildingId " +
            "AND CONCAT(t.apartment.apartmentNumber, ' ', t.description, ' ', t.taxType) like :keyword")
    List<TaxEntity> findAllTaxesByBuildingIdFilteredByKeywordOrderByDueDateAscAndTaxStatus(@Param("buildingId") Long buildingId,
                                                                                           @Param("keyword") String searchKeyword);
    @Query("select t FROM TaxEntity  as t where t.building.id = :buildingId AND t.apartment.owner.username = :ownerUsername " +
            "Order by t.taxStatus desc, t.dueDate asc")
    List<TaxEntity> findAllTaxesByBuildingIdAndOwnerUsername(Long buildingId, String ownerUsername);
}
