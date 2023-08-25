package bg.propertymanager.service.impl;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("Should return null when the role name is not found")
    void findRoleByNameWhenRoleNameIsNotFound() {
        String roleName = "ADMIN";

        RoleEntity roleEntity = roleService.findRoleByName(roleName);

        assertNull(roleEntity);
    }

    @Test
    @DisplayName("Should return the role when the role name is found")
    void findRoleByNameWhenRoleNameIsFound() {
        String roleName = "ADMIN";
        RoleEntity roleEntity = new RoleEntity();
        when(roleRepository.findByName(roleName)).thenReturn(roleEntity);

        RoleEntity result = roleService.findRoleByName(roleName);

        assertEquals(roleEntity, result);
    }

    @Test
    @DisplayName("Should return all Roles as list")
    void testFindAllRoles_ShouldReturnAllRoles() {
        List<RoleEntity> expectedRoles = new ArrayList<>();
        addRoles(expectedRoles);

        when(this.roleRepository.findAll()).thenReturn(expectedRoles);

        List<RoleEntity> actualRoles = this.roleService.findAll();

        assertEquals(expectedRoles, actualRoles);
        if (expectedRoles.size() == actualRoles.size()) {
            for (int i = 0; i < expectedRoles.size(); i++) {
                int finalI = i;
                assertAll(
                        () -> assertEquals(expectedRoles.get(finalI).getId(), actualRoles.get(finalI).getId()),
                        () -> assertEquals(expectedRoles.get(finalI).getRole(), actualRoles.get(finalI).getRole()),
                        () -> assertEquals(expectedRoles.get(finalI).getName(), actualRoles.get(finalI).getName())
                );
            }
        }

    }

    private static void addRoles(List<RoleEntity> roles) {
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
}