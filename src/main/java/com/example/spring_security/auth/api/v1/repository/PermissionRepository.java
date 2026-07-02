package com.example.spring_security.auth.api.v1.repository;

import com.example.spring_security.auth.api.v1.entity.Permission;
import com.example.spring_security.auth.api.v1.entity.PermissionName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionName name);
}
