package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.repository.RoleRepository;
import bg.propertymanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService appUserDetailsService;
    private final String adminPass;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService appUserDetailsService,
                       @Value("${app.default.admin.password}") String adminPass) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserDetailsService = appUserDetailsService;
        this.adminPass = adminPass;
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
                .setCountry("Bulgaria")
                .setCity("Plovdiv")
                .setStreet("Vasil Levski 34");
        userRepository.save(admin);
    }

//    public void registerAndLogin(UserRegisterDTO userRegisterDTO) {
//        UserEntity newUser =
//                new UserEntity().
//                        setEmail(userRegisterDTO.getEmail()).
//                        setFirstName(userRegisterDTO.getFirstName()).
//                        setLastName(userRegisterDTO.getLastName()).
//                        setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
//
//        userRepository.save(newUser);
//
//        UserDetails userDetails =
//                appUserDetailsService.loadUserByUsername(newUser.getEmail());
//
//        Authentication auth =
//                new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        userDetails.getPassword(),
//                        userDetails.getAuthorities()
//                );
//
//        SecurityContextHolder.
//                getContext().
//                setAuthentication(auth);
//    }
}
