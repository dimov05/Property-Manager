package bg.propertymanager.repository;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.ExpenseEntity;
import bg.propertymanager.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
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

    @Query("SELECT e FROM ExpenseEntity as e WHERE e.building.id = :buildingId ORDER BY e.taxStatus desc, e.dueDate asc ")
    List<ExpenseEntity> findAllExpensesByBuildingOrderByDueDateAsc(Long buildingId);
}
