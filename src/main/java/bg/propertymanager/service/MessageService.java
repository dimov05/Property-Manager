package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.message.MessageAddDTO;
import bg.propertymanager.model.dto.message.MessageEditDTO;
import bg.propertymanager.model.entity.MessageEntity;

import java.util.List;

public interface MessageService {
    List<MessageEntity> findAllNeighboursMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building);

    List<MessageEntity> findAllManagersMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building);

    void addMessage(MessageAddDTO messageAddDTO, Long buildingId, String authorUsername);

    MessageEntity findById(Long messageId);

    void updateMessage(MessageEditDTO messageEditDTO);

    void deleteMessageWithId(Long messageId);

    String findDateOfLastMessageFromManagerByBuilding(BuildingViewDTO building);

    List<MessageEntity> findAllMessagesForBuildingByOwnerIdSortedFromNewToOld(Long buildingId, String authorUsername);

    //Used for @PreAuthorize
    Boolean checkIfUserIsAuthor(String authorUsername, Long messageId);
}
