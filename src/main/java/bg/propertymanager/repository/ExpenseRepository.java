package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    Set<ExpenseEntity> findAllByBuilding_Id(Long buildingId);

    @Query("SELECT SUM(e.amount)  FROM ExpenseEntity as e WHERE e.building.id = :buildingId AND e.taxStatus = 'PAID'")
    Optional<BigDecimal> findAmountOfPaidExpensesByBuildingId(Long buildingId);

    @Query("SELECT SUM(e.amount)  FROM ExpenseEntity as e " +
            "WHERE e.building.id = :buildingId AND e.taxStatus = 'UNPAID' OR e.taxStatus = 'UNCONFIRMED'")
    Optional<BigDecimal> findAmountOfUnpaidExpensesByBuildingId(Long buildingId);
}
