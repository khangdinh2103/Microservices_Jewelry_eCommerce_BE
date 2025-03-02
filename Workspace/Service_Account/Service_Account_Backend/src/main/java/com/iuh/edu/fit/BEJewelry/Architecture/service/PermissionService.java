package com.iuh.edu.fit.BEJewelry.Architecture.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Permission;

import com.iuh.edu.fit.BEJewelry.Architecture.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Permission fetchById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent())
            return permissionOptional.get();
        return null;
    }

    public Permission create(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission update(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null) {
            permissionDB.setName(p.getName());
            permissionDB.setApiPath(p.getApiPath());
            permissionDB.setMethod(p.getMethod());
            permissionDB.setModule(p.getModule());

            // update
            permissionDB = this.permissionRepository.save(permissionDB);
            return permissionDB;
        }
        return null;
    }
}
