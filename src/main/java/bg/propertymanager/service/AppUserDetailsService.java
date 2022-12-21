package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.user.AppUserDetails;
import bg.propertymanager.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(this::mapUser)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " does not exist!"));
    }

    private AppUserDetails mapUser(UserEntity userEntity) {
        return new AppUserDetails(userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getEmail(),
                userEntity.getPhoneNumber(),
                userEntity.getFullName(),
                userEntity.getCountry(),
                userEntity.getCity(),
                userEntity.getStreet(),
                userEntity.
                        getRoles().
                        stream().
                        map(this::mapRole).
                        toList());
    }

    private GrantedAuthority mapRole(RoleEntity userRole) {
        return new SimpleGrantedAuthority("ROLE_" + userRole.getRole().name());
    }
}
