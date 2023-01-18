package bg.propertymanager.service;

import bg.propertymanager.model.dto.user.PasswordChangeDTO;
import bg.propertymanager.model.dto.user.UserEditDTO;
import bg.propertymanager.model.dto.user.UserRegisterDTO;
import bg.propertymanager.model.entity.*;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.model.view.UserEntityViewModel;
import bg.propertymanager.repository.RoleRepository;
import bg.propertymanager.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final String adminPass;
    private final BuildingService buildingService;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper, @Value("${app.default.admin.password}") String adminPass, @Lazy BuildingService buildingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.adminPass = adminPass;
        this.buildingService = buildingService;
    }

    public UserEntity register(UserRegisterDTO userRegisterDTO) {
        UserEntity newUser =
                new UserEntity()
                        .setUsername(userRegisterDTO.getUsername())
                        .setRoles(List.of(roleRepository.getById(2L)))
                        .setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()))
                        .setEmail(userRegisterDTO.getEmail())
                        .setPhoneNumber(userRegisterDTO.getPhoneNumber())
                        .setFullName(userRegisterDTO.getFullName())
                        .setCountry(userRegisterDTO.getCountry())
                        .setCity(userRegisterDTO.getCity())
                        .setStreet(userRegisterDTO.getStreet())
                        .setRegistrationDate(LocalDate.now())
                        .setManagerInBuildings(Collections.emptySet())
                        .setOwnerInBuildings(Collections.emptySet())
                        .setApartments(Collections.emptySet())
                        .setMessages(Collections.emptySet());

        userRepository.save(newUser);

        return newUser;
    }


    public void init() {
        if (userRepository.count() == 0 && roleRepository.count() == 0) {
            RoleEntity adminRole = new RoleEntity().setRole(UserRolesEnum.ADMIN);
            RoleEntity userRole = new RoleEntity().setRole(UserRolesEnum.USER);

            adminRole = roleRepository.save(adminRole);
            userRole = roleRepository.save(userRole);

            initAdmin(List.of(adminRole, userRole));
            initUser(List.of(userRole));
        }
    }

    private void initUser(List<RoleEntity> roles) {
        UserEntity user = new UserEntity()
                .setRoles(roles)
                .setUsername("user")
                .setPassword(passwordEncoder.encode("user"))
                .setFullName("Dimo Dimov")
                .setEmail("user@user.com")
                .setPhoneNumber("+359878123456")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Vasil Levski 37")
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet())
                .setRegistrationDate(LocalDate.now());
        userRepository.save(user);
    }


    private void initAdmin(List<RoleEntity> roles) {
        UserEntity admin = new UserEntity()
                .setRoles(roles)
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
                .setRegistrationDate(LocalDate.now());
        userRepository.save(admin);
    }

    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    public void updateProfile(UserEditDTO userEditDTO) {
        UserEntity userToSave = userRepository
                .findById(userEditDTO.getId())
                .orElseThrow(() -> new NullPointerException("The user you are searching is null"));
        userToSave
                .setEmail(userEditDTO.getEmail())
                .setPhoneNumber(userEditDTO.getPhoneNumber())
                .setCountry(userEditDTO.getCountry())
                .setCity(userEditDTO.getCity())
                .setStreet(userEditDTO.getStreet());
        userRepository.save(userToSave);
    }

    public void updatePassword(PasswordChangeDTO passwordChangeDTO) {
        UserEntity userToChangePassword = userRepository
                .findById(passwordChangeDTO.getId())
                .orElseThrow(() -> new NullPointerException("The user you are searching is null"));
        userToChangePassword.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(userToChangePassword);
    }

    public boolean checkIfOldPasswordIsCorrect(PasswordChangeDTO passwordChangeDTO, Principal principal) {
        String actualOldPassword = this.findUserByUsername(principal.getName()).getPassword();
        return passwordEncoder.matches(passwordChangeDTO.getOldPassword(), actualOldPassword);
    }

    public List<UserEntityViewModel> findAll() {
        List<UserEntity> usersList = userRepository.findAll();
        return usersList
                .stream()
                .map(u -> modelMapper.map(u, UserEntityViewModel.class))
                .toList();
    }

    public UserEntity findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("No such user with this Id"));
    }

    public void removeApartmentFromUser(ApartmentEntity apartment, UserEntity ownerToRemove) {
        UserEntity userToUpdate = findById(ownerToRemove.getId());
        userToUpdate.getApartments().remove(apartment);
        userRepository.save(userToUpdate);
    }

    public void addApartmentToUser(ApartmentEntity apartment, UserEntity ownerToAdd) {
        UserEntity userToUpdate = findById(ownerToAdd.getId());
        userToUpdate.getApartments().add(apartment);
        userRepository.save(userToUpdate);
    }

    public void removeManagerRightsInBuilding(UserEntity currentManager, BuildingEntity building) {
        UserEntity userToUpdate = findById(currentManager.getId());
        userToUpdate.getManagerInBuildings().remove(building);
        userRepository.save(userToUpdate);
    }

    public void addManagerRightsInBuilding(UserEntity managerToSet, BuildingEntity buildingToSave) {
        UserEntity userToUpdate = findById(managerToSet.getId());
        userToUpdate.getManagerInBuildings().add(buildingToSave);
        userRepository.save(userToUpdate);
    }

    public void addBuildingToUser(UserEntity ownerToAdd, BuildingEntity building) {
        UserEntity userToUpdate = findById(ownerToAdd.getId());
        userToUpdate.getOwnerInBuildings().add(building);
        userRepository.save(userToUpdate);
    }

    public void removeBuildingFromUser(UserEntity ownerToRemove, BuildingEntity building) {
        UserEntity userToUpdate = findById(ownerToRemove.getId());
        boolean ownerHaveMoreApartmentsInThisBuilding = false;
        for (ApartmentEntity apartment : building.getApartments()) {
            if (apartment.getOwner().equals(ownerToRemove)) {
                ownerHaveMoreApartmentsInThisBuilding = true;
                break;
            }
        }
        if (!ownerHaveMoreApartmentsInThisBuilding) {
            userToUpdate.getOwnerInBuildings().remove(building);
            userRepository.save(userToUpdate);
        }
    }

    public void addMessageToUser(MessageEntity newMessage, UserEntity author) {
        author.getMessages().add(newMessage);
        userRepository.save(author);
    }

    public void removeMessageFromUser(MessageEntity messageToRemove, UserEntity author) {
        author.getMessages().remove(messageToRemove);
        userRepository.save(author);
    }

    public Page<UserEntityViewModel> findAllNeighboursByBuildingPaginated(Pageable pageable, Long buildingId) {
        BuildingEntity building = buildingService.findEntityById(buildingId);
        List<UserEntityViewModel> neighbours = getNeighboursInBuilding(building);
        return getPageOfUsers(pageable, neighbours);
    }

    public Page<UserEntityViewModel> findAllUsersWithFilterPaginated(Pageable pageable, String searchKeyword) {
        List<UserEntityViewModel> users = getUsersWithOrWithoutFilter(searchKeyword);
        return getPageOfUsers(pageable, users);
    }

    private List<UserEntityViewModel> getUsersWithOrWithoutFilter(String searchKeyword) {
        List<UserEntityViewModel> users;
        if (searchKeyword == null) {
            users = userRepository
                    .findAll()
                    .stream()
                    .map(u -> modelMapper.map(u, UserEntityViewModel.class))
                    .toList();
        } else {
            users = userRepository
                    .findAllByKeyword(searchKeyword)
                    .stream()
                    .map(u -> modelMapper.map(u, UserEntityViewModel.class))
                    .toList();
        }
        return users;
    }

    private List<UserEntityViewModel> getNeighboursInBuilding(BuildingEntity building) {
        List<UserEntityViewModel> neighbours = userRepository
                .findAllNeighboursInBuilding(building)
                .stream()
                .map(u -> modelMapper.map(u, UserEntityViewModel.class))
                .toList();
        return neighbours;
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
}
