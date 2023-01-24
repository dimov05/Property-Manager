package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;

public interface RoleService {
    RoleEntity findRoleByName(String roleName);
}
