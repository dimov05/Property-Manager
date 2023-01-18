package bg.propertymanager.repository;

import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllByBuilding_IdAndAuthorIdOrderByCreatedDateDesc(Long buildingId, Long managerId);

    List<MessageEntity> findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(Long buildingId, UserEntity manager);

    @Query("SELECT m.createdDate FROM MessageEntity as m " +
            "WHERE m.building.id = :buildingId AND m.author.id = :managerId " +
            "order by m.createdDate DESC")
    LocalDateTime findDateOfLastMessageFromManagerByBuildingId(@Param("buildingId") Long buildingId,@Param("managerId") Long managerId);

    List<MessageEntity> findALlByBuilding_IdAndAuthor_UsernameOrderByCreatedDateDesc(Long buildingId, String authorUsername);
}
