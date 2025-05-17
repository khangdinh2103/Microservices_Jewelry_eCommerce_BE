package com.iuh.edu.fit.BEJewelry.Architecture.service;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Permission;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.Meta;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResultPaginationDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.PermissionRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        return roleOptional.orElse(null);
    }

    /**
     * Get a role by its name
     *
     * @param name the name of the role to find
     * @return the Role object if found, null otherwise
     */
    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    private List<Permission> resolvePermissions(List<Permission> permissions) {
        if (permissions == null) {
            return null;
        }

        List<Long> reqPermissions = permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toList());

        return this.permissionRepository.findByIdIn(reqPermissions);
    }

    public Role create(Role r) {
        if (r.getPermissions() != null) {
            r.setPermissions(resolvePermissions(r.getPermissions()));
        }
        return this.roleRepository.save(r);
    }

    public Role update(Role r) {
        Role roleDB = this.fetchById(r.getId());
        if (roleDB == null) {
            return null;
        }

        if (r.getPermissions() != null) {
            r.setPermissions(resolvePermissions(r.getPermissions()));
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());
        roleDB.setPermissions(r.getPermissions());

        return this.roleRepository.save(roleDB);
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pRole = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new com.iuh.edu.fit.BEJewelry.Architecture.domain.response.Meta(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                pRole.getTotalPages(),
                pRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pRole.getContent());
        return rs;
    }
}