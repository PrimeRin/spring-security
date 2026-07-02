package com.example.spring_security.auth.api.v1.repository;

import com.example.spring_security.auth.api.v1.entity.Role;
import com.example.spring_security.auth.api.v1.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
