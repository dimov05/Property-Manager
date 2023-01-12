package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    Set<ExpenseEntity> findAllByBuilding_Id(Long buildingId);
}
