package bg.propertymanager.service;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}
