package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Set;

@Repository
public interface TaxRepository extends JpaRepository<TaxEntity, Long> {
    Set<TaxEntity> findAllByBuilding_Id(Long buildingId);

    @Query("SELECT SUM(t.amount)  FROM TaxEntity as t WHERE t.building.id = :buildingId AND t.taxStatus = 'PAID'")
    BigDecimal findBalanceByBuildingId(@Param("buildingId") Long buildingId);
}
