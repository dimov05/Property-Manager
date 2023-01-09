package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ApartmentRepository extends JpaRepository<ApartmentEntity, Long> {
    Set<ApartmentEntity> findAllByBuildingId(Long buildingId);
}
