package bg.propertymanager.repository;

import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Set<MessageEntity> findAllByBuilding_IdAndAuthorIdOrderByCreatedDateDesc(Long buildingId,Long managerId);

    Set<MessageEntity> findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(Long buildingId, UserEntity manager);
}
