package bg.propertymanager.service.impl;

import bg.propertymanager.model.entity.RoleEntity;
import bg.propertymanager.repository.RoleRepository;
import bg.propertymanager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}
