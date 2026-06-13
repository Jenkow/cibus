package com.jll.cibus.role.repository;

import com.jll.cibus.role.enums.Roles;
import com.jll.cibus.role.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>
{
    Optional<RoleEntity> findByRole(Roles role);
}
