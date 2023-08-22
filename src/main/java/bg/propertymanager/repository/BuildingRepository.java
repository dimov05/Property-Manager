package bg.propertymanager.repository;

import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<BuildingEntity, Long> {
    @Query("SELECT b FROM BuildingEntity as b " +
            "WHERE CONCAT(b.id, ' ', b.name, ' ', b.city, ' ',b.country , ' ',b.street) like %?1%")
    List<BuildingEntity> findAllByKeyword(String searchKeyword);

    List<BuildingEntity> fi

}
