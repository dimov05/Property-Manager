package bg.propertymanager.service;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.repository.MessageRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public Set<MessageEntity> findAllNeighboursMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building) {
        return messageRepository
                .findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(building.getId(), building.getManager());

    }

    public Set<MessageEntity> findAllManagersMessagesForBuildingSortedFromNewToOld(BuildingViewDTO building) {
        return messageRepository
                .findAllByBuilding_IdAndAuthorIdOrderByCreatedDateDesc(building.getId(), building.getManager().getId());
    }
}
