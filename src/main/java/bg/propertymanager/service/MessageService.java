package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.message.MessageAddDTO;
import bg.propertymanager.model.dto.message.MessageEditDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.MessageRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final BuildingService buildingService;
    private final UserService userService;

    public MessageService(MessageRepository messageRepository, BuildingService buildingService, UserService userService) {
        this.messageRepository = messageRepository;
        this.buildingService = buildingService;
        this.userService = userService;
    }


    public Set<MessageEntity> findAllNeighboursMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building) {
        return messageRepository
                .findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(building.getId(), building.getManager());

    }

    public Set<MessageEntity> findAllManagersMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building) {
        return messageRepository
                .findAllByBuilding_IdAndAuthorIdOrderByCreatedDateDesc(building.getId(), building.getManager().getId());
    }

    public void addMessage(MessageAddDTO messageAddDTO, Long buildingId) {
        BuildingEntity building = buildingService.findEntityById(buildingId);
        UserEntity author = building.getManager();
        MessageEntity newMessage = new MessageEntity()
                .setAuthor(author)
                .setBuilding(building)
                .setTitle(messageAddDTO.getTitle())
                .setContent(messageAddDTO.getContent())
                .setCreatedDate(LocalDateTime.now());

        buildingService.addMessage(newMessage, building);
        userService.addMessageToUser(newMessage, author);

        messageRepository.save(newMessage);
    }

    public MessageEntity findById(Long messageId) {
        return messageRepository
                .findById(messageId)
                .orElseThrow(() -> new NullPointerException("No message with this Id"));
    }

    public void updateMessage(MessageEditDTO messageEditDTO) {
        MessageEntity messageToUpdate = findById(messageEditDTO.getId());
        messageToUpdate
                .setTitle(messageEditDTO.getTitle())
                .setContent(messageEditDTO.getContent());
        messageRepository.save(messageToUpdate);
    }

    public void deleteMessageWithId(Long messageId, Long buildingId) {
        MessageEntity messageToRemove = findById(messageId);
        UserEntity author = messageToRemove.getAuthor();
        BuildingEntity building = messageToRemove.getBuilding();

        buildingService.removeMessageFromBuilding(messageToRemove, building);
        userService.removeMessageFromUser(messageToRemove, author);

        messageRepository.delete(messageToRemove);
    }
}
