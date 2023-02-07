package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.message.MessageAddDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.MessageRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BuildingService buildingService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    @DisplayName("Should save the message when the building and author exist")
    void addMessageWhenBuildingAndAuthorExist() {
        MessageEntity message = new MessageEntity();
        BuildingEntity building = new BuildingEntity();
        UserEntity author = new UserEntity();
        when(buildingService.findEntityById(anyLong())).thenReturn(building);
        when(userService.findUserByUsername(anyString())).thenReturn(author);
        when(messageRepository.save(any())).thenReturn(message);

        messageService.addMessage(new MessageAddDTO(), 1L, "test");

        verify(buildingService, times(1)).findEntityById(anyLong());
        verify(userService, times(1)).findUserByUsername(anyString());
        verify(messageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return all messages from manager for building sorted from new to old")
    void
    findAllManagersMessagesForBuildingSortedFromNewToOldShouldReturnAllMessagesFromManagerForBuildingSortedFromNewToOld() {
        BuildingViewDTO building =
                new BuildingViewDTO()
                        .setId(1L)
                        .setManager(new UserEntity().setId(1L).setUsername("manager"));

        List<MessageEntity> messages = new ArrayList<>();
        messages.add(
                new MessageEntity()
                        .setId(1L)
                        .setAuthor(new UserEntity().setId(1L).setUsername("manager")));

        when(messageRepository.findAllByBuilding_IdAndAuthorIdOrderByCreatedDateDesc(
                building.getId(), building.getManager().getId()))
                .thenReturn(messages);

        List<MessageEntity> actual =
                messageService.findAllManagersMessagesForBuildingSortedFromNewToOld(building);

        assertEquals(messages, actual);
    }

    @Test
    @DisplayName("Should return all messages for building sorted from new to old")
    void
    findAllNeighboursMessagesForBuildingSortedFromNewToOldShouldReturnAllMessagesForBuildingSortedFromNewToOld() {
        BuildingViewDTO building =
                new BuildingViewDTO()
                        .setId(1L)
                        .setManager(new UserEntity().setId(1L).setUsername("manager"));

        List<MessageEntity> messages = new ArrayList<>();
        messages.add(
                new MessageEntity()
                        .setId(1L)
                        .setAuthor(new UserEntity().setId(2L).setUsername("user")));

        when(messageRepository.findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(
                building.getId(), building.getManager()))
                .thenReturn(messages);

        List<MessageEntity> actual =
                messageService.findAllNeighboursMessagesForBuildingSortedFromNewToOld(building);

        assertEquals(messages, actual);
    }
}