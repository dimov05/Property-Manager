package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.building.BuildingViewDTO;
import bg.propertymanager.model.dto.message.MessageAddDTO;
import bg.propertymanager.model.dto.message.MessageEditDTO;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.MessageEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.MessageRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BuildingService buildingService;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private static final long INDEX_ONE = 1L;

    @Test
    @DisplayName("Should save the message when the building and author exist")
    void addMessageWhenBuildingAndAuthorExist() {
        MessageEntity message = new MessageEntity();
        BuildingEntity building = new BuildingEntity();
        UserEntity author = new UserEntity();
        when(buildingService.findEntityById(anyLong())).thenReturn(building);
        when(userService.findUserByUsername(anyString())).thenReturn(author);
        when(messageRepository.save(any())).thenReturn(message);

        messageService.addMessage(new MessageAddDTO(), INDEX_ONE, "test");

        verify(buildingService, times(1)).findEntityById(anyLong());
        verify(userService, times(1)).findUserByUsername(anyString());
        verify(messageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return all messages from manager for building sorted from new to old")
    void
    findAllManagersMessagesForBuildingSortedFromNewToOldShouldReturnAllMessagesFromManagerForBuildingSortedFromNewToOld() {
        UserEntity manager = initManager();
        BuildingViewDTO building = initBuilding(manager);

        List<MessageEntity> messages = new ArrayList<>();
        addMessagesByAuthor(messages, manager);

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
    findAllNeighboursMessagesForBuildingSortedFromNewToOld_ShouldReturnAllMessagesForBuildingSortedFromNewToOld() {
        UserEntity manager = initManager();
        BuildingViewDTO building = initBuilding(manager);

        List<MessageEntity> messages = new ArrayList<>();
        UserEntity user = initUser();
        addMessagesByAuthor(messages, user);

        when(messageRepository.findAllByBuilding_IdAndAuthorIsNotOrderByCreatedDateDesc(
                building.getId(), building.getManager()))
                .thenReturn(messages);

        List<MessageEntity> actual =
                messageService.findAllNeighboursMessagesForBuildingSortedFromNewToOld(building);

        assertEquals(messages, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "6"})
    @DisplayName("Testing findById() should return message with the chosen id")
    void findById_ShouldReturnMessageWithCorrectId(long id) {
        UserEntity user = initUser();
        MessageEntity message = initMessageWithIdAndAuthor(id, user);

        when(messageRepository.findById(id)).thenReturn(Optional.of(message));

        MessageEntity actual = messageService.findById(id);

        assertAll(
                () -> assertEquals(message.getId(), actual.getId()),
                () -> assertEquals(message.getAuthor(), actual.getAuthor()),
                () -> assertEquals(message.getTitle(), actual.getTitle()),
                () -> assertEquals(message.getContent(), actual.getContent()),
                () -> assertEquals(message.getCreatedDate(), actual.getCreatedDate())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "-2", "-3", "-12312"})
    @DisplayName("Testing if findById() returns exception on incorrect id")
    void testFindById_ShouldThrowException_WhenIncorrectMessageIdIsPassed(Long id) {
        assertThrows(NullPointerException.class, () -> this.messageService.findById(id));
    }

    @Test
    @DisplayName("Should update message on method call")
    void testUpdateMessage_ShouldUpdateMessage() {
        UserEntity user = initUser();
        MessageEntity existingMessage = initMessageWithIdAndAuthor(INDEX_ONE, user);
        MessageEditDTO updatedMessage = new MessageEditDTO()
                .setId(INDEX_ONE)
                .setContent("Updated content")
                .setTitle("Updated title");
        when(messageRepository.findById(INDEX_ONE)).thenReturn(Optional.ofNullable(existingMessage));
        messageService.updateMessage(updatedMessage);

        verify(this.messageRepository, times(1)).findById(INDEX_ONE);
        if (existingMessage != null) {
            verify(this.messageRepository, times(1)).save(existingMessage);
        }

        assert existingMessage.getContent().equals("Updated content");
        assert existingMessage.getTitle().equals("Updated title");
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "6", "8", "9"})
    @DisplayName("Should delete message on method call")
    void testDeleteMessageWithId_ShouldDeleteMessageWithId(long id) {
        UserEntity user = initUser();
        UserEntity manager = initManager();
        BuildingEntity building = new BuildingEntity()
                .setId(INDEX_ONE);
        MessageEntity existingMessage = initMessageWithIdAndAuthor(id, user);
        existingMessage.setBuilding(building);
        when(messageRepository.findById(id)).thenReturn(Optional.ofNullable(existingMessage));

        messageService.deleteMessageWithId(id);

        verify(this.buildingService, times(1)).removeMessageFromBuilding(existingMessage, building);
        verify(this.userService, times(1)).removeMessageFromUser(existingMessage, user);
        verify(this.messageRepository, times(1)).delete(existingMessage);
    }

    @Test
    void findDateOfLastMessageFromManagerByBuilding_ShouldReturnStringDateWithCorrectInput() {
        UserEntity manager = initManager();
        BuildingViewDTO building = initBuilding(manager);
        List<MessageEntity> messages = new ArrayList<>();
        addMessagesByAuthor(messages, manager);
        List<LocalDateTime> dates = messages.stream().map(MessageEntity::getCreatedDate).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        String expectedDate = String.format("%s-%s-%s",
                messages.get(2).getCreatedDate().getYear(),
                messages.get(2).getCreatedDate().getMonthValue(),
                messages.get(2).getCreatedDate().getDayOfMonth());
        when(this.messageRepository.findDateOfLastMessageFromManagerByBuildingId(building.getId(), manager.getId()))
                .thenReturn(dates);
        String actualDate = this.messageService.findDateOfLastMessageFromManagerByBuilding(building);

        assertEquals(expectedDate, actualDate);
    }

    @Test
    void findDateOfLastMessageFromManagerByBuilding_ShouldReturnNoMessagesString_WhenThereAreNoMessages() {
        UserEntity manager = initManager();
        BuildingViewDTO building = initBuilding(manager);
        List<MessageEntity> messages = new ArrayList<>();
        List<LocalDateTime> dates = messages.stream().map(MessageEntity::getCreatedDate).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        String expected = dates.isEmpty() ? "No messages" : "False";
        when(this.messageRepository.findDateOfLastMessageFromManagerByBuildingId(building.getId(), manager.getId()))
                .thenReturn(dates);
        String actual = this.messageService.findDateOfLastMessageFromManagerByBuilding(building);

        assertEquals(expected, actual);

    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "5", "6"})
    void findAllMessagesForBuildingByOwnerIdSortedFromNewToOld_ShouldReturnCorrectData(long id) {
        UserEntity user = initUser();
        UserEntity manager = initManager();
        BuildingViewDTO building = initBuilding(manager);
        List<MessageEntity> messages = new ArrayList<>();
        addMessagesByAuthor(messages, user);
        List<MessageEntity> expected = messages
                .stream()
                .sorted(Comparator.comparing(MessageEntity::getCreatedDate))
                .collect(Collectors.toList());

        when(messageRepository.findALlByBuilding_IdAndAuthor_UsernameOrderByCreatedDateDesc(building.getId(), user.getUsername()))
                .thenReturn(expected);

        List<MessageEntity> actual = this.messageService.findAllMessagesForBuildingByOwnerIdSortedFromNewToOld(building.getId(), user.getUsername());
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            int finalI = i;
            assertAll(
                    () -> assertEquals(expected.get(finalI).getId(), actual.get(finalI).getId()),
                    () -> assertEquals(expected.get(finalI).getAuthor(), actual.get(finalI).getAuthor()),
                    () -> assertEquals(expected.get(finalI).getCreatedDate(), actual.get(finalI).getCreatedDate()),
                    () -> assertEquals(expected.get(finalI).getTitle(), actual.get(finalI).getTitle()),
                    () -> assertEquals(expected.get(finalI).getContent(), actual.get(finalI).getContent())
            );
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "6"})
    void testIfUserIsAuthor_ShouldReturnTrueOnCorrectData(long id) {
        UserEntity user = initUser();
        MessageEntity message = initMessageWithIdAndAuthor(id, user);
        when(this.messageRepository.findById(id)).thenReturn(Optional.ofNullable(message));
        boolean expected = true;

        assert message != null;
        boolean actual = this.messageService.checkIfUserIsAuthor(user.getUsername(), message.getId());

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "6"})
    void testIfUserIsAuthor_ShouldThrowExceptionIfThereIsNoMessageWithThisId(long id) {
        assertThrows(NullPointerException.class, () -> this.messageService.findById(id));
    }

    @Test
    void testIfUserIsAuthor_ShouldReturnFalseIfTheUserIsNotTheAuthorOfThisMessage() {
        UserEntity author = initUser();
        UserEntity notAuthor = initManager();
        MessageEntity message = initMessageWithIdAndAuthor(1, author);
        when(this.messageRepository.findById(message.getId())).thenReturn(Optional.ofNullable(message));
        boolean expected = false;

        boolean actual = this.messageService.checkIfUserIsAuthor(notAuthor.getUsername(), message.getId());

        assertEquals(expected, actual);
    }

//    public Boolean checkIfUserIsAuthor(String authorUsername, Long messageId) {
//        MessageEntity message = messageRepository
//                .findById(messageId)
//                .orElseThrow(() -> new NullPointerException("There is no message with this id " + messageId));
//        return message.getAuthor().getUsername().equals(authorUsername);
//    }

    private static MessageEntity initMessageWithIdAndAuthor(long id, UserEntity user) {
        return new MessageEntity()
                .setId(id)
                .setContent("Random content")
                .setCreatedDate(LocalDateTime.now())
                .setAuthor(user)
                .setTitle("Message " + id);
    }

    private static UserEntity initUser() {
        return new UserEntity()
                .setId(2L)
                .setUsername("user")
                .setCity("Plovdiv")
                .setCountry("Bulgaria")
                .setEmail("user@mail.com")
                .setFullName("User Userov");
    }

    private static BuildingViewDTO initBuilding(UserEntity manager) {
        return new BuildingViewDTO()
                .setId(INDEX_ONE)
                .setManager(manager);
    }

    private static UserEntity initManager() {
        return new UserEntity()
                .setId(INDEX_ONE)
                .setUsername("manager")
                .setCity("Plovdiv")
                .setCountry("Bulgaria")
                .setEmail("manager@mail.com")
                .setFullName("Manager Managerov");
    }

    private static void addMessagesByAuthor(List<MessageEntity> messages, UserEntity author) {
        messages.add(
                new MessageEntity()
                        .setId(INDEX_ONE)
                        .setAuthor(author)
                        .setCreatedDate(LocalDateTime.of(2023, Month.AUGUST, 5, 6, 30))
                        .setTitle("Message 1")
                        .setContent("Content 1"));
        messages.add(
                new MessageEntity()
                        .setId(2L)
                        .setAuthor(author)
                        .setCreatedDate(LocalDateTime.of(2023, Month.AUGUST, 7, 8, 22))
                        .setTitle("Message 2")
                        .setContent("Content 2"));
        messages.add(
                new MessageEntity()
                        .setId(3L)
                        .setAuthor(author)
                        .setCreatedDate(LocalDateTime.of(2023, Month.AUGUST, 8, 2, 54))
                        .setTitle("Message 3")
                        .setContent("Content 3"));
    }
}