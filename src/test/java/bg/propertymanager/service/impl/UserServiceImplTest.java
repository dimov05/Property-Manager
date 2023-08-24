package bg.propertymanager.service.impl;

import bg.propertymanager.model.dto.user.PasswordChangeDTO;
import bg.propertymanager.model.dto.user.UserEditDTO;
import bg.propertymanager.model.dto.user.UserRegisterDTO;
import bg.propertymanager.model.entity.*;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.model.view.UserEntityViewModel;
import bg.propertymanager.repository.BuildingRepository;
import bg.propertymanager.repository.RoleRepository;
import bg.propertymanager.repository.UserRepository;
import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.RoleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;
    @Value("admin")
    private String adminPass;
    @Mock
    private BuildingService buildingService;
    @Mock
    private RoleService roleService;

    static List<RoleEntity> roles;
    static UserEntity user;

    @BeforeAll
    static void setup() {
        roles = new ArrayList<>();
        user = new UserEntity();
        initRoles();
        initUser();

    }

    @Test
    void testRegister_ShouldRegisterAndSaveNewUser() {
        UserRegisterDTO userToRegister = initUserRegisterDto(new UserRegisterDTO());

        when(roleRepository.getById(2L)).thenReturn(roles.get(0));
        UserEntity registeredUser = this.userService.register(userToRegister);
        verify(this.userRepository, times(1)).save(any());

        assertAll(
                () -> assertEquals(registeredUser.getId(), user.getId()),
                () -> assertEquals(registeredUser.getEmail(), user.getEmail()),
                () -> assertEquals(registeredUser.getPhoneNumber(), user.getPhoneNumber()),
                () -> assertEquals(registeredUser.getFullName(), user.getFullName()),
                () -> assertEquals(registeredUser.getCountry(), user.getCountry()),
                () -> assertEquals(registeredUser.getCity(), user.getCity()),
                () -> assertEquals(registeredUser.getStreet(), user.getStreet())
        );

    }


    private UserEntity initUserForInitMethod(RoleEntity userRole, LocalDate date) {
        return new UserEntity()
                .setRoles(List.of(userRole))
                .setUsername("manager")
                .setPassword(passwordEncoder.encode("123456"))
                .setFullName("Dimo Dimov")
                .setEmail("manager@manager.com")
                .setPhoneNumber("+359878123456")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Vasil Levski 37")
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet())
                .setRegistrationDate(date);
    }


//    @Test
//    void testInitMethod_ShouldInitOneUserAndOneAdminToDatabase() throws NoSuchMethodException {
//        when(userRepository.count()).thenReturn(0L);
//        when(roleRepository.count()).thenReturn(0L);
//
//        RoleEntity adminRole = roles.get(2);
//        RoleEntity userRole = roles.get(0);
//        LocalDate date = LocalDate.now();
//        UserEntity userToInit = initUserForInitMethod(userRole, date);
//        UserEntity adminToInit = initAdminForInitMethod(userRole, adminRole, date);
////        when(roleRepository.save(adminRole)).thenReturn(adminRole);
////        when(roleRepository.save(userRole)).thenReturn(userRole);
//
//        this.userService.init();
//
//        verify(roleRepository, times(1)).save(adminRole);
//        verify(roleRepository, times(1)).save(userRole);
//
//        verify(userRepository, times(1)).save(adminToInit);
//        verify(userRepository, times(1)).save(userToInit);
//    }
    //    public void init() {
//        if (userRepository.count() == 0 && roleRepository.count() == 0) {
//            RoleEntity adminRole = new RoleEntity().setRole(UserRolesEnum.ADMIN);
//            RoleEntity userRole = new RoleEntity().setRole(UserRolesEnum.USER);
//
//            adminRole = roleRepository.save(adminRole);
//            userRole = roleRepository.save(userRole);
//
//            initAdmin(List.of(adminRole, userRole));
//            initAdmin(List.of(adminRole, userRole));
//            initUser(List.of(userRole));
//        }
//    }

    @Test
    void testInitUser_ShouldInitUserToDatabase() {
        RoleEntity userRole = roles.get(0);
        LocalDate date = LocalDate.now();
        UserEntity userToInit = initUserForInitMethod(userRole, date);

        this.userService.initUser(List.of(userRole));

        verify(this.userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testInitAdmin_ShouldInitAdminToDatabase() {
        RoleEntity userRole = roles.get(0);
        RoleEntity adminRole = roles.get(2);
        LocalDate date = LocalDate.now();
        UserEntity adminToInit = initAdminForInitMethod(userRole, adminRole, date);

        this.userService.initAdmin(List.of(adminRole, userRole));

        verify(this.userRepository, times(1)).save(any(UserEntity.class));
    }

    @ParameterizedTest
    @CsvSource(value = {"user1", "user2", "user4", "user6", "user1231"})
    void testFindUserByUsername_ShouldReturnCorrectUser_OnCorrectUsername(String username) {
        LocalDate date = LocalDate.now();
        UserEntity expected = initUserForInitMethod(username, roles.get(0), date);
        when(this.userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(expected));

        UserEntity actual = this.userService.findUserByUsername(username);

        assertAll(
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber()),
                () -> assertEquals(expected.getFullName(), actual.getFullName()),
                () -> assertEquals(expected.getCountry(), actual.getCountry()),
                () -> assertEquals(expected.getCity(), actual.getCity()),
                () -> assertEquals(expected.getStreet(), actual.getStreet()));

    }

    @Test
    void testFindUserByUsername_ShouldReturnNull_OnNotFoundUserWithThisUsername() {
        when(this.userRepository.findByUsername("missingUser")).thenReturn(Optional.empty());
        UserEntity actual = this.userService.findUserByUsername("missingUser");
        assertNull(actual);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "3", "5", "4", "22"})
    void testUpdateProfile_ShouldUpdateProfile_OnExistingProfileWithThisId(long id) {
        UserEditDTO userToUpdate = new UserEditDTO()
                .setId(id)
                .setUsername("testUsername");
        UserEntity returnUserFromRepo = new UserEntity().setId(id).setUsername("testUsername");
        when(this.userRepository.findById(id)).thenReturn(Optional.ofNullable(returnUserFromRepo));
        this.userService.updateProfile(userToUpdate);
        verify(this.userRepository, times(1)).findById(id);
        verify(this.userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUpdateProfile_ShouldThrowException_OnNotExistingProfileWithThisId() {
        UserEditDTO userToUpdate = new UserEditDTO()
                .setId(1L)
                .setUsername("testUsername");
        assertThrows(NullPointerException.class, () -> this.userService.updateProfile(userToUpdate));
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "3", "5", "4", "22"})
    void testUpdatePassword_ShouldUpdatePassword_OnExistingProfileWithThisId(long id) {
        UserEntity userToChangePassword = new UserEntity()
                .setId(id)
                .setPassword("oldPassword");
        PasswordChangeDTO passwordToUpdate = new PasswordChangeDTO()
                .setId(id)
                .setNewPassword("newPassword")
                .setOldPassword("oldPassword");
        when(this.userRepository.findById(id)).thenReturn(Optional.of(userToChangePassword));
        UserEntity userWithOldPassword = this.userRepository.findById(id).get();
        assertEquals(userWithOldPassword.getPassword(), "oldPassword");

        UserEntity userWithNewPassword = userWithOldPassword.setPassword("newPassword");
        when(this.userRepository.findById(id)).thenReturn(Optional.of(userWithNewPassword));
        assertEquals(userWithOldPassword.getPassword(), "newPassword");

        this.userService.updatePassword(passwordToUpdate);
        verify(this.userRepository, times(2)).findById(id);
        verify(this.userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUpdatePassword_ShouldThrowException_OnNotExistingProfileWithThisId() {
        PasswordChangeDTO passwordToUpdate = new PasswordChangeDTO()
                .setId(1L)
                .setNewPassword("newPassword")
                .setOldPassword("oldPassword");
        assertThrows(NullPointerException.class, () -> this.userService.updatePassword(passwordToUpdate));
    }

    @Test
    @WithMockUser(username = "user")
    void testCheckIfOldPasswordIsCorrect_ShouldReturnTrueOnCorrectOldPassword() {
        UserEntity userToReturn = new UserEntity()
                .setId(1L)
                .setUsername("user")
                .setPassword("password");
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO()
                .setOldPassword("password");
        when(this.userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(userToReturn));
        this.userService.checkIfOldPasswordIsCorrect(passwordChangeDTO, getPrincipal());
        String actualOldPassword = this.userRepository.findByUsername("user").get().getPassword();
        assertEquals(actualOldPassword, userToReturn.getPassword());

    }

    @Test
    void testFindAllUsers_ShouldReturnAllUsersViewModel() {
        List<UserEntity> users = new ArrayList<>();
        addUsers(users);
        when(this.userRepository.findAll()).thenReturn(users);

        List<UserEntityViewModel> actual = this.userService.findAll();

        assertEquals(users.size(), actual.size());
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "6"})
    void testFindById_ShouldReturnUserWithCorrectId(long id) {
        UserEntity user = initUserForInitMethod(roles.get(0), LocalDate.now()).setId(id);

        when(this.userRepository.findById(id)).thenReturn(Optional.ofNullable(user));

        UserEntity actual = this.userService.findById(id);

        assertAll(
                () -> assertEquals(user.getId(), actual.getId()),
                () -> assertEquals(user.getUsername(), actual.getUsername()),
                () -> assertEquals(user.getFullName(), actual.getFullName()),
                () -> assertEquals(user.getCity(), actual.getCity()),
                () -> assertEquals(user.getCountry(), actual.getCountry()),
                () -> assertEquals(user.getEmail(), actual.getEmail()),
                () -> assertEquals(user.getPhoneNumber(), actual.getPhoneNumber()),
                () -> assertEquals(user.getStreet(), actual.getStreet())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "-2", "-3", "-12312"})
    void testFindById_ShouldThrowException_WhenIncorrectUserIdIsPassed(Long id) {
        assertThrows(NullPointerException.class, () -> this.userService.findById(id));
    }


    @Test
    void testRemoveApartmentFromUser_ShouldRemoveApartmentFromUser() {
        ApartmentEntity apartmentToRemove = new ApartmentEntity()
                .setApartmentNumber("2A")
                .setId(1L);
        UserEntity ownerToRemove = new UserEntity().setId(1L).setUsername("user")
                .setApartments(new HashSet<>());
        when(this.userRepository.findById(1L)).thenReturn(Optional.ofNullable(ownerToRemove));
        this.userService.removeApartmentFromUser(apartmentToRemove, ownerToRemove);
        verify(this.userRepository, times(1)).save(ownerToRemove);
    }

    @ParameterizedTest
    @CsvSource(value = {"1,1A", "2,2B", "3,3C", "5,5J", "12,12A"})
    void testAddApartmentToUser_ShouldAddApartmentToUser(long id, String apartmentNumber) {
        ApartmentEntity apartmentToAdd = new ApartmentEntity()
                .setApartmentNumber(apartmentNumber)
                .setId(id);
        UserEntity ownerToAdd = new UserEntity().setId(id).setUsername("user")
                .setApartments(new HashSet<>());
        when(this.userRepository.findById(id)).thenReturn(Optional.ofNullable(ownerToAdd));
        this.userService.addApartmentToUser(apartmentToAdd, ownerToAdd);
        verify(this.userRepository, times(1)).save(ownerToAdd);
    }

    @Test
    void testRemoveManagerRightInBuildingForUser_ShouldRemoveManagerRightsForUserInBuilding() {
        BuildingEntity building = new BuildingEntity()
                .setId(1L);
        Set<BuildingEntity> managerInBuildings = new HashSet<>();
        managerInBuildings.add(building);
        UserEntity manager = new UserEntity()
                .setId(1L)
                .setRoles(List.of(roles.get(1)))
                .setManagerInBuildings(managerInBuildings);
        when(this.userRepository.findById(1L)).thenReturn(Optional.ofNullable(manager));
        this.userService.removeManagerRightsInBuilding(manager, building);
        verify(this.userRepository, times(1)).save(manager);
        assertFalse(manager.getManagerInBuildings().contains(building));
    }

    @Test
    void testAddManagerRightInBuildingForUser_ShouldAddManagerRightsForUserInBuilding() {
        BuildingEntity building = new BuildingEntity()
                .setId(1L);
        Set<BuildingEntity> managerInBuildings = new HashSet<>();
        UserEntity manager = new UserEntity()
                .setId(1L)
                .setRoles(List.of(roles.get(1)))
                .setManagerInBuildings(managerInBuildings);
        when(this.userRepository.findById(1L)).thenReturn(Optional.ofNullable(manager));
        this.userService.addManagerRightsInBuilding(manager, building);
        verify(this.userRepository, times(1)).save(manager);
        assert manager.getManagerInBuildings().contains(building);
    }

    @Test
    void testAddBuildingToUser_ShouldAddBuildingToUser() {
        BuildingEntity building = new BuildingEntity()
                .setId(1L);
        Set<BuildingEntity> ownerInBuildings = new HashSet<>();
        UserEntity user = new UserEntity()
                .setId(1L)
                .setRoles(List.of(roles.get(1)))
                .setOwnerInBuildings(ownerInBuildings);
        when(this.userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        this.userService.addBuildingToUser(user, building);
        verify(this.userRepository, times(1)).save(user);
        assert user.getOwnerInBuildings().contains(building);
    }

    @Test
    void testRemoveBuildingFromUser_ShouldRemoveBuildingFromUser_WhenUserHasNoMoreApartmentsInThisBuilding() {
        UserEntity ownerToRemove = new UserEntity()
                .setId(1L);
        BuildingEntity building = new BuildingEntity()
                .setId(1L);
        ApartmentEntity apartmentWithOwner = new ApartmentEntity()
                .setOwner(ownerToRemove);
        ApartmentEntity apartmentWithOwner2 = new ApartmentEntity()
                .setOwner(ownerToRemove);
        ApartmentEntity apartmentWithoutOwner = new ApartmentEntity()
                .setOwner(new UserEntity());
        building.setApartments(Set.of(apartmentWithoutOwner, apartmentWithOwner, apartmentWithOwner2));

        UserEntity userToUpdate = new UserEntity()
                .setId(ownerToRemove.getId())
                .setOwnerInBuildings(new HashSet<>());
        userToUpdate.getOwnerInBuildings().add(building);


        when(this.userRepository.findById(ownerToRemove.getId())).thenReturn(Optional.ofNullable(userToUpdate));
        this.userService.removeBuildingFromUser(userToUpdate, building);
        verify(this.userRepository, times(1)).findById(ownerToRemove.getId());
        verify(this.userRepository, times(1)).save(userToUpdate);
        assert !userToUpdate.getOwnerInBuildings().contains(building);
    }

    @Test
    void testRemoveBuildingFromUser_ShouldNotRemoveBuildingFromUser_WhenUserHasMoreApartmentsInThisBuilding() {
        UserEntity ownerToRemove = new UserEntity()
                .setId(1L);
        BuildingEntity building = new BuildingEntity()
                .setId(1L);
        ApartmentEntity apartmentWithOwner = new ApartmentEntity()
                .setOwner(ownerToRemove);
        ApartmentEntity apartmentWithoutOwner = new ApartmentEntity()
                .setOwner(new UserEntity());
        building.setApartments(Set.of(apartmentWithOwner));

        UserEntity userToUpdate = new UserEntity()
                .setId(ownerToRemove.getId())
                .setOwnerInBuildings(new HashSet<>());
        userToUpdate.getOwnerInBuildings().add(building);


        when(this.userRepository.findById(ownerToRemove.getId())).thenReturn(Optional.ofNullable(userToUpdate));
        this.userService.removeBuildingFromUser(ownerToRemove, building);
        verify(this.userRepository, times(1)).findById(ownerToRemove.getId());
        verify(this.userRepository, times(1)).save(userToUpdate);
        assert userToUpdate.getOwnerInBuildings().contains(building);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "5", "12"})
    void testAddMessageToUser_ShouldAddMessageToUser(long id) {
        MessageEntity messageToAdd = new MessageEntity()
                .setId(id);
        UserEntity author = new UserEntity().setId(id)
                .setMessages(new HashSet<>());

        this.userService.addMessageToUser(messageToAdd, author);
        verify(this.userRepository, times(1)).save(author);
        assert author.getMessages().contains(messageToAdd);
    }

    @ParameterizedTest
    @CsvSource(value = {"1", "2", "3", "5", "12"})
    void testRemoveMessageFromUser_ShouldRemoveMessageFromUser(long id) {
        MessageEntity messageToRemove = new MessageEntity()
                .setId(id);
        UserEntity author = new UserEntity().setId(id)
                .setMessages(new HashSet<>());
        author.getMessages().add(messageToRemove);

        this.userService.removeMessageFromUser(messageToRemove, author);
        verify(this.userRepository, times(1)).save(author);
        assert !author.getMessages().contains(messageToRemove);
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "3"})
    void testFindAllUsersWithFilterPaginated_ShouldReturnPageOfUsers() {
        List<UserEntity> users = new ArrayList<>();
        initUsers(users);
        String keyword = "3";
        if (keyword == null) {
            when(this.userRepository.findAll()).thenReturn(users);
        } else {
            when(this.userRepository.findAllByKeyword(keyword)).thenReturn(List.of(users.get(2)));
        }

        this.userService.findAllUsersWithFilterPaginated(PageRequest.of(1, 5), keyword);
        if (keyword == null) {
            verify(this.userRepository, times(1)).findAll();
        } else {
            verify(this.userRepository, times(1)).findAllByKeyword(keyword);
        }
    }

    @Test
    @Disabled
    void testFindAllNeighboursByBuildingPaginated_ShouldReturnPageOfNeighbours() {
        List<UserEntity> users = new ArrayList<>();
        initUsers(users);
        BuildingEntity building = new BuildingEntity()
                .setId(1L)
                .setNeighbours(new HashSet<>());
        building.getNeighbours().addAll(users);
        List<UserEntityViewModel> neighbours = Collections.singletonList(modelMapper.map(users, UserEntityViewModel.class));

        when(this.buildingRepository.findById(building.getId())).thenReturn(Optional.of(building));
        when(this.userRepository.findAllNeighboursInBuilding(building)).thenReturn(users);

        Page<UserEntityViewModel> allNeighboursByBuildingPaginated = this.userService.findAllNeighboursByBuildingPaginated(PageRequest.of(1, 5), building.getId());
        verify(this.buildingRepository, times(1)).findById(building.getId());
        verify(this.userRepository, times(1)).findAllNeighboursInBuilding(building);
        Page<UserEntityViewModel> expected = getPageOfUsers(PageRequest.of(1, 5), neighbours);
        assertEquals(allNeighboursByBuildingPaginated.get(), expected.get());

    }

    @ParameterizedTest
    @CsvSource(value = {"0,USER", "1,MANAGER", "2,ADMIN"})
    void testChangeRole_ShouldChangeRoleToAnyRole(long id,String roleName) {
        RoleEntity adminRole = roles.get(2);
        RoleEntity managerRole = roles.get(1);
        RoleEntity userRole = roles.get(0);
        RoleEntity roleToReturn = new RoleEntity()
                .setId(id)
                .setName(roleName);
        if(roleName.equals("USER")){
            roleToReturn.setRole(UserRolesEnum.USER);
        } else if (roleName.equals("MANAGER")){
            roleToReturn.setRole(UserRolesEnum.MANAGER);
        } else {
            roleToReturn.setRole(UserRolesEnum.ADMIN);
        }
        UserEntity userToChangeRole = new UserEntity()
                .setId(1L)
                .setUsername("username")
                .setRoles(new ArrayList<>());

        when(this.userRepository.findByUsername("username")).thenReturn(Optional.ofNullable(userToChangeRole));
        when(this.roleService.findRoleByName(roleName)).thenReturn(roleToReturn);
        when(this.roleService.findRoleByName("ADMIN")).thenReturn(adminRole);
//        RoleEntity roleToSet = roles.get(0);
//        if(roleToSet.getName().equals("USER") || roleToSet.getName().equals("MANAGER")){
//            if(userToChangeRole.getRoles().contains(adminRole)){
//                userToChangeRole.getRoles().clear();
//                userToChangeRole.getRoles().add(roleToSet);
//            }
//        } else {
//            userToChangeRole.getRoles().clear();
//            userToChangeRole.getRoles().addAll(roles);
//        }
        assert userToChangeRole != null;
        this.userService.changeRole(roleName,userToChangeRole.getUsername());
        verify(this.userRepository,times(1)).findByUsername(userToChangeRole.getUsername());
        verify(this.roleService,times(2)).findRoleByName(anyString());
    }
    @Test
    void testChangeRole_ShouldChangeRoleToAnyRole() {
        RoleEntity adminRole = roles.get(2);
        UserEntity userToChangeRole = new UserEntity()
                .setId(1L)
                .setUsername("username")
                .setRoles(List.of(adminRole));

        when(this.userRepository.findByUsername("username")).thenReturn(Optional.ofNullable(userToChangeRole));
        when(this.roleService.findRoleByName("USER")).thenReturn(adminRole);
//        RoleEntity roleToSet = roles.get(0);
//        if(roleToSet.getName().equals("USER") || roleToSet.getName().equals("MANAGER")){
//            if(userToChangeRole.getRoles().contains(adminRole)){
//                userToChangeRole.getRoles().clear();
//                userToChangeRole.getRoles().add(roleToSet);
//            }
//        } else {
//            userToChangeRole.getRoles().clear();
//            userToChangeRole.getRoles().addAll(roles);
//        }
        assert userToChangeRole != null;
        this.userService.changeRole("USER",userToChangeRole.getUsername());
        verify(this.userRepository,times(1)).findByUsername(userToChangeRole.getUsername());
        verify(this.roleService,times(2)).findRoleByName(anyString());
    }

//    @Override
//    public void changeRole(String roleName, String username) {
//        UserEntity userToChangeRole = userRepository.findByUsername(username)
//                .orElseThrow(() -> new NullPointerException("No user with this username " + username));
//        RoleEntity roleToSet = roleService.findRoleByName(roleName);
//        RoleEntity adminRole = roleService.findRoleByName("ADMIN");
//
//        if (roleToSet.getName().equals("USER")) {
//            if (checkIfUserIsAdmin(userToChangeRole, adminRole)) {
//                userToChangeRole.getRoles().remove(adminRole);
//                userToChangeRole.getRoles().removeAll(List.of(adminRole));
//                userToChangeRole.setRoles(List.of(roleToSet));
//            }
//        } else {
//            if (!checkIfUserIsAdmin(userToChangeRole, adminRole)) {
//                userToChangeRole.getRoles().add(roleToSet);
//            }
//        }
//
//    }

    @Test
    @Disabled
    void testChangeRole_ShouldChangeRoleToManager() {

    }


    @Test
    @Disabled
    void testChangeRole_ShouldChangeRoleToAdmin() {

    }

    @Test
    void testChangeRole_ShouldThrowNullPointerException_OnMissingUserWithThisUsername() {
        assertThrows(NullPointerException.class, () -> this.userService.changeRole("ADMIN", "missingUser"));
    }


    private List<UserEntityViewModel> getUsersWithOrWithoutFilter(String searchKeyword, List<UserEntity> users) {
        List<UserEntityViewModel> usersView;
        if (searchKeyword == null) {
            usersView = users
                    .stream()
                    .map(u -> modelMapper.map(u, UserEntityViewModel.class))
                    .toList();
        } else {
            usersView = users
                    .stream()
                    .filter(u -> u.getId() == 3)
                    .map(u -> modelMapper.map(u, UserEntityViewModel.class))
                    .toList();
        }
        return usersView;
    }

    private static PageImpl<UserEntityViewModel> getPageOfUsers(Pageable pageable, List<UserEntityViewModel> neighbours) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<UserEntityViewModel> list = getUsersPaginated(neighbours, pageSize, startItem);
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), neighbours.size());
    }

    private static List<UserEntityViewModel> getUsersPaginated(List<UserEntityViewModel> users, int pageSize, int startItem) {
        List<UserEntityViewModel> list;
        if (users.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            list = users.subList(startItem, toIndex);
        }
        return list;
    }


    private static void initUser() {
        user
                .setUsername("User")
                .setRoles(List.of(roles.get(2)))
                .setPassword("password")
                .setEmail("user@mail.com")
                .setPhoneNumber("0888999999")
                .setFullName("User Userov")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Street Sl")
                .setRegistrationDate(LocalDate.of(2023, Month.AUGUST, 10))
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet());
    }

    private static void initRoles() {
        roles.add(new RoleEntity()
                .setId(1L)
                .setName("USER")
                .setRole(UserRolesEnum.USER));
        roles.add(new RoleEntity()
                .setId(2L)
                .setName("MANAGER")
                .setRole(UserRolesEnum.MANAGER));
        roles.add(new RoleEntity()
                .setId(3L)
                .setName("ADMIN")
                .setRole(UserRolesEnum.ADMIN));
    }

    private UserEntity initAdminForInitMethod(RoleEntity userRole, RoleEntity adminRole, LocalDate date) {
        return new UserEntity()
                .setRoles(List.of(adminRole, userRole))
                .setUsername("admin")
                .setPassword(passwordEncoder.encode(adminPass))
                .setFullName("Admin Adminov")
                .setEmail("admin@admin.com")
                .setPhoneNumber("+359877779292")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Vasil Levski 34")
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet())
                .setRegistrationDate(date);
    }

    private UserRegisterDTO initUserRegisterDto(UserRegisterDTO userRegisterDTO) {
        return userRegisterDTO.setUsername("User")
                .setPassword("password")
                .setMatchingPassword("password")
                .setEmail("user@mail.com")
                .setPhoneNumber("0888999999")
                .setFullName("User Userov")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Street Sl");
    }

    private static Principal getPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return "user";
            }
        };
    }

    private void addUsers(List<UserEntity> users) {
        UserEntity user1 = initUserForInitMethod("user1", roles.get(0), LocalDate.now());
        UserEntity user2 = initUserForInitMethod("user2", roles.get(0), LocalDate.now());
        users.add(user1);
        users.add(user2);
    }

    private UserEntity initUserForInitMethod(String username, RoleEntity userRole, LocalDate date) {
        return new UserEntity()
                .setRoles(List.of(userRole))
                .setUsername(username)
                .setPassword(passwordEncoder.encode("123456"))
                .setFullName("Dimo Dimov")
                .setEmail("manager@manager.com")
                .setPhoneNumber("+359878123456")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Vasil Levski 37")
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet())
                .setRegistrationDate(date);
    }

    private static void initUsers(List<UserEntity> users) {
        UserEntity user1 = new UserEntity().setId(1L);
        UserEntity user2 = new UserEntity().setId(2L);
        UserEntity user3 = new UserEntity().setId(3L);
        UserEntity user4 = new UserEntity().setId(4L);
        UserEntity user5 = new UserEntity().setId(5L);
        UserEntity user6 = new UserEntity().setId(6L);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
    }
}