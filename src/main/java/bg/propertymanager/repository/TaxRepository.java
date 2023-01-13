package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TaxRepository extends JpaRepository<TaxEntity, Long> {
    Set<TaxEntity> findAllByBuilding_Id(Long buildingId);

    @Query("SELECT SUM(t.amount)  FROM TaxEntity as t WHERE t.building.id = :buildingId AND t.taxStatus = 'PAID'")
    Optional<BigDecimal> findAmountOfPaidTaxesByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT SUM(t.amount) FROM TaxEntity  as t WHERE t.apartment.id = :apartmentId AND t.taxStatus = 'UNPAID'")
    BigDecimal findOwedMoneyByApartmentId(@Param("apartmentId") Long apartmentId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TaxEntity t " +
            "WHERE t.taxStatus = 'PAID' AND t.expense.id = :expenseId")
    Boolean existsByTaxStatusPaidAndExpenseId(@Param("expenseId") Long expenseId);
}
