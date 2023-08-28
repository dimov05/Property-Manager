package bg.propertymanager.service.impl;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.model.enums.UserRolesEnum;
import bg.propertymanager.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static bg.propertymanager.util.TestDataUtils.*;
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

    @ParameterizedTest
    @CsvSource(value = {"USER","ADMIN","MANAGER"})
    @DisplayName("Should return the role when the role name is found")
    void findRoleByNameWhenRoleNameIsFound(String roleName) {
        RoleEntity roleEntity = new RoleEntity().setName(roleName);
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
        assertIterableEquals(expectedRoles,actualRoles);
    }

    private static void addRoles(List<RoleEntity> roles) {
        roles.add(new RoleEntity()
                .setId(INDEX_ONE)
                .setName("USER")
                .setRole(UserRolesEnum.USER));
        roles.add(new RoleEntity()
                .setId(INDEX_TWO)
                .setName("MANAGER")
                .setRole(UserRolesEnum.MANAGER));
        roles.add(new RoleEntity()
                .setId(INDEX_THREE)
                .setName("ADMIN")
                .setRole(UserRolesEnum.ADMIN));
    }
}