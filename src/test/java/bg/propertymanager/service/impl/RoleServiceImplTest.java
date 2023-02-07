package bg.propertymanager.service.impl;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
}