package com.jll.cibus.user.repository;

import com.jll.cibus.user.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity,Long>
{
    Optional<UserRoleEntity> findByName (String name);
    boolean existsByName(String name);

    @Query("""
    SELECT COUNT (u)
        FROM UserEntity u
        WHERE u.role.name = :role
    """)
    Long countByUserRole (@Param("role") String role);
}
