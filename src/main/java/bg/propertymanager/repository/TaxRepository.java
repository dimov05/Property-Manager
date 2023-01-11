package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TaxRepository extends JpaRepository<TaxEntity, Long> {
    Set<TaxEntity> findAllByBuilding_Id(Long buildingId);
}
