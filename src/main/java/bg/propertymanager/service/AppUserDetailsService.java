package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(this::mapUser)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " does not exist!"));
    }

    private UserDetails mapUser(UserEntity userEntity) {
        return User
                .builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(userEntity
                        .getRoles()
                        .stream()
                        .map(this::mapRole)
                        .collect(Collectors.toList()))
                .build();
    }

    private GrantedAuthority mapRole(RoleEntity userRole) {
        return new SimpleGrantedAuthority("ROLE_" + userRole.getRole().name());
    }
}
