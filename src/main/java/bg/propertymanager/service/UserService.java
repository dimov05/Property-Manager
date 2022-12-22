package bg.propertymanager.service;

import bg.propertymanager.model.dto.UserRegisterDTO;
import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.model.view.UserProfileView;
import bg.propertymanager.repository.RoleRepository;
import bg.propertymanager.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService appUserDetailsService;
    private final ModelMapper modelMapper;
    private final String adminPass;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService appUserDetailsService,
                       ModelMapper modelMapper, @Value("${app.default.admin.password}") String adminPass) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserDetailsService = appUserDetailsService;
        this.modelMapper = modelMapper;
        this.adminPass = adminPass;
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
                        .setStreet(userRegisterDTO.getStreet());

        userRepository.save(newUser);

        return newUser;
    }

    public UserProfileView findUserById(long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            return modelMapper.map(user, UserProfileView.class);
        }
        throw new NullPointerException("No profile existing with this id");
    }
    public Long findUserIdByUsername(String name){
        Optional<UserEntity> user = userRepository.findByUsername(name);
        if (user.isPresent()) {
            return user.get().getId();
        }
        throw new NullPointerException("No profile existing with this name");
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
}
