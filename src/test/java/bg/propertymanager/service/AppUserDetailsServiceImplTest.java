package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.model.user.AppUserDetails;
import bg.propertymanager.repository.UserRepository;
import bg.propertymanager.service.impl.AppUserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class AppUserDetailsServiceImplTest {

    @Mock
    private UserRepository mockUserRepo;
    private AppUserDetailsServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new AppUserDetailsServiceImpl(mockUserRepo);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange
        UserEntity testUserEntity = new UserEntity()
                .setRoles(List.of(new RoleEntity().setRole(UserRolesEnum.ADMIN),
                        new RoleEntity().setRole(UserRolesEnum.USER)))
                .setUsername("pesho")
                .setPassword("123456")
                .setEmail("pesho@abv.bg")
                .setCountry("Builgaria")
                .setFullName("Pesho Petrov")
                .setCity("Sofia")
                .setStreet("Ulitsa 55");
        when(mockUserRepo.findByUsername(testUserEntity.getUsername()))
                .thenReturn(Optional.of(testUserEntity));
        // Act
        AppUserDetails userDetails = toTest.loadUserByUsername(testUserEntity.getUsername());
        // Assert
        Assertions.assertEquals(testUserEntity.getUsername(), userDetails.getUsername());
        Assertions.assertEquals(testUserEntity.getEmail(), userDetails.getEmail());
        Assertions.assertEquals(testUserEntity.getPassword(), userDetails.getPassword());
        Assertions.assertEquals(testUserEntity.getCity(), userDetails.getCity());
        Assertions.assertEquals(testUserEntity.getCountry(), userDetails.getCountry());
        Assertions.assertEquals(testUserEntity.getStreet(), userDetails.getStreet());
        Assertions.assertEquals(testUserEntity.getFullName(), userDetails.getFullName());

        Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
        Assertions.assertEquals(2, authorities.size());
        Iterator<GrantedAuthority> authoritiesIter = authorities.iterator();
        Assertions.assertEquals("ROLE_" + UserRolesEnum.ADMIN.name(), authoritiesIter.next().getAuthority());
        Assertions.assertEquals("ROLE_" + UserRolesEnum.USER.name(), authoritiesIter.next().getAuthority());

    }

    @Test
    void testLoadUserByUsername_UserDoesNotExists() {
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> toTest.loadUserByUsername("non-existing"));
    }
}
