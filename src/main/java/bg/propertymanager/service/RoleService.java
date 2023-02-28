package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    RoleEntity findRoleByName(String roleName);

    List<RoleEntity> findAll();
}
