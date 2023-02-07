package bg.propertymanager.repository;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity as u WHERE :building IN elements(u.ownerInBuildings) ORDER BY u.id")
    List<UserEntity> findAllNeighboursInBuilding(BuildingEntity building);

    @Query("SELECT u FROM UserEntity as u WHERE CONCAT(u.id, ' ', u.username, ' ', u.fullName) like %?1%")
    List<UserEntity> findAllByKeyword(String searchKeyword);
}
