package bg.propertymanager.util;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class TestDataUtils {
    private UserRepository userRepository;
    private TaxRepository taxRepository;
    private RoleRepository roleRepository;
    private MessageRepository messageRepository;
    private ExpenseRepository expenseRepository;
    private BuildingRepository buildingRepository;
    private ApartmentRepository apartmentRepository;
    private PasswordEncoder passwordEncoder;
    public static final long INDEX_ONE = 1L;
    public static final long INDEX_TWO = 2L;
    public static final long INDEX_THREE = 3L;
    public static final String NEW_PASSWORD = "newPassoword";
    public static final String OLD_PASSWORD = "oldPassword";

    public TestDataUtils(UserRepository userRepository, TaxRepository taxRepository,
                         RoleRepository roleRepository, MessageRepository messageRepository,
                         ExpenseRepository expenseRepository, BuildingRepository buildingRepository,
                         ApartmentRepository apartmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taxRepository = taxRepository;
        this.roleRepository = roleRepository;
        this.messageRepository = messageRepository;
        this.expenseRepository = expenseRepository;
        this.buildingRepository = buildingRepository;
        this.apartmentRepository = apartmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private List<RoleEntity> initRoles() {
        if (roleRepository.count() == 0) {
            RoleEntity adminRole = new RoleEntity().setRole(UserRolesEnum.ADMIN);
            RoleEntity userRole = new RoleEntity().setRole(UserRolesEnum.USER);
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
        }
        return roleRepository.findAll();
    }

    public UserEntity createTestAdmin(String username) {
        initRoles();
        UserEntity admin = new UserEntity()
                .setRoles(roleRepository.findAll())
                .setUsername(username)
                .setPassword(passwordEncoder.encode("123456"))
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
        return admin;
    }

    public UserEntity createTestUser(String username) {
        initRoles();
        UserEntity user = new UserEntity()
                .setRoles(roleRepository.findAll())
                .setUsername(username)
                .setPassword(passwordEncoder.encode("123456"))
                .setFullName("User Userov")
                .setEmail("user@user.com")
                .setPhoneNumber("+359877779292")
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Vasil Levski 34")
                .setManagerInBuildings(Collections.emptySet())
                .setOwnerInBuildings(Collections.emptySet())
                .setApartments(Collections.emptySet())
                .setMessages(Collections.emptySet())
                .setRegistrationDate(LocalDate.now());
        userRepository.save(user);
        return user;
    }

    public void cleanUpDatabase() {
        userRepository.deleteAll();
        taxRepository.deleteAll();
        roleRepository.deleteAll();
        messageRepository.deleteAll();
        expenseRepository.deleteAll();
        buildingRepository.deleteAll();
        apartmentRepository.deleteAll();
    }
}
