package bg.propertymanager.service;

import bg.propertymanager.model.dto.apartment.ApartmentEditDTO;
import bg.propertymanager.model.dto.user.PasswordChangeDTO;
import bg.propertymanager.model.dto.user.UserEditDTO;
import bg.propertymanager.model.dto.user.UserRegisterDTO;
import bg.propertymanager.model.entity.ApartmentEntity;
import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.model.view.AdminViewUserProfile;
import bg.propertymanager.repository.ApartmentRepository;
import bg.propertymanager.repository.RoleRepository;
import bg.propertymanager.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService appUserDetailsService;
    private final ModelMapper modelMapper;
    private final String adminPass;
    private final BuildingService buildingService;
    private final ApartmentRepository apartmentRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService appUserDetailsService,
                       ModelMapper modelMapper, @Value("${app.default.admin.password}") String adminPass, BuildingService buildingService,
                       ApartmentRepository apartmentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserDetailsService = appUserDetailsService;
        this.modelMapper = modelMapper;
        this.adminPass = adminPass;
        this.buildingService = buildingService;
        this.apartmentRepository = apartmentRepository;
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
                        .setRegistrationDate(LocalDate.now());

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
                .setStreet("Vasil Levski 37");
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
                .setStreet("Vasil Levski 34");
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

    public List<AdminViewUserProfile> findAll() {
        List<UserEntity> usersList = userRepository.findAll();
        return usersList
                .stream()
                .map(u -> modelMapper.map(u, AdminViewUserProfile.class))
                .toList();
    }

    public UserEntity findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("No such user with this Id"));
    }

    private void updateUser(UserEntity userToUpdate) {
        userRepository.save(userToUpdate);
    }

    private BuildingEntity getBuildingEntity(Long buildingId) {
        return buildingService.findEntityById(buildingId);
    }

    public void removeApartmentFromUser(ApartmentEntity apartment, UserEntity ownerToRemove) {
        UserEntity userToUpdate = findById(ownerToRemove.getId());
        userToUpdate.getApartments().remove(apartment);
        updateUser(userToUpdate);
    }

    public void addApartmentToUser(ApartmentEntity apartment, UserEntity ownerToAdd) {
        UserEntity userToUpdate = findById(ownerToAdd.getId());
        userToUpdate.getApartments().add(apartment);
        updateUser(userToUpdate);
    }
}
