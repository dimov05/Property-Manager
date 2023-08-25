package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.message.MessageAddDTO;
import bg.propertymanager.model.dto.message.MessageEditDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.MessageRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.MessageService;
import bg.propertymanager.service.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final BuildingService buildingService;
    private final UserService userService;

    public MessageServiceImpl(MessageRepository messageRepository, BuildingService buildingService, UserService userService) {
        this.messageRepository = messageRepository;
        this.buildingService = buildingService;
        this.userService = userService;
    }

    @Override
    public List<MessageEntity> findAllNeighboursMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building) {
        return messageRepository
                .findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(building.getId(), building.getManager());

    }

    @Override
    public List<MessageEntity> findAllManagersMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building) {
        return messageRepository
                .findAllByBuilding_IdAndAuthorIdOrderByCreatedDateDesc(building.getId(), building.getManager().getId());
    }

    @Override
    public void addMessage(MessageAddDTO messageAddDTO, Long buildingId, String authorUsername) {
        BuildingEntity building = buildingService.findEntityById(buildingId);
        UserEntity author = userService.findUserByUsername(authorUsername);
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

    @Override
    public MessageEntity findById(Long messageId) {
        return messageRepository
                .findById(messageId)
                .orElseThrow(() -> new NullPointerException("No message with this Id"));
    }

    @Override
    public void updateMessage(MessageEditDTO messageEditDTO) {
        MessageEntity messageToUpdate = findById(messageEditDTO.getId());
        messageToUpdate
                .setTitle(messageEditDTO.getTitle())
                .setContent(messageEditDTO.getContent());
        messageRepository.save(messageToUpdate);
    }

    @Override
    public void deleteMessageWithId(Long messageId) {
        MessageEntity messageToRemove = findById(messageId);
        UserEntity author = messageToRemove.getAuthor();
        BuildingEntity building = messageToRemove.getBuilding();

        buildingService.removeMessageFromBuilding(messageToRemove, building);
        userService.removeMessageFromUser(messageToRemove, author);

        messageRepository.delete(messageToRemove);
    }

    @Override
    public String findDateOfLastMessageFromManagerByBuilding(BuildingViewDTO building) {
        List<LocalDateTime> dateOfLastMessageFromManager = messageRepository
                .findDateOfLastMessageFromManagerByBuildingId(building.getId(), building.getManager().getId());
        if (dateOfLastMessageFromManager.isEmpty()) {
            return "No messages";
        } else {
            StringBuilder date = new StringBuilder();
            date
                    .append(dateOfLastMessageFromManager.get(0).getYear())
                    .append("-")
                    .append(dateOfLastMessageFromManager.get(0).getMonthValue())
                    .append("-")
                    .append(dateOfLastMessageFromManager.get(0).getDayOfMonth());
            return date.toString();
        }
    }

    @Override
    public List<MessageEntity> findAllMessagesForBuildingByOwnerIdSortedFromNewToOld(Long buildingId, String authorUsername) {
        return messageRepository.findALlByBuilding_IdAndAuthor_UsernameOrderByCreatedDateDesc(buildingId, authorUsername);
    }

    @Override
    public Boolean checkIfUserIsAuthor(String authorUsername, Long messageId) {
        MessageEntity message = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new NullPointerException("There is no message with this id " + messageId));
        return message.getAuthor().getUsername().equals(authorUsername);
    }
}
