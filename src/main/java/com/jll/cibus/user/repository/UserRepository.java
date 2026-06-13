package com.jll.cibus.user.repository;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.user.entity.UserRoleEntity;
import com.jll.cibus.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity>
{

    List<UserEntity> findByFirstName (String firstName);

    List<UserEntity> findByLastName (String lastName);

    List<UserEntity> findByFirstNameAndLastName (String firstName, String lastName);

    Optional<UserEntity> findByPhoneNumber (String phoneNumber);

    Optional<UserEntity> findByEmail (String email);

    Optional<UserEntity> findByDni (Long dni);

    List<UserEntity> findByBranch (BranchEntity branch);

    List<UserEntity> findByBranchId (Long branchId);

    List<UserEntity> findByRole (UserRoleEntity role);

    List<UserEntity> findByRoleId (Long id);
    
    boolean existsByDni (Long dni);

    boolean existsByDniAndBranchId(Long dni, Long branchId);

    List<UserEntity> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    boolean existsByEmail (String email);

    @Query("""
    SELECT COUNT (u)
        FROM UserEntity u
        WHERE u.branch.id = :branchId
        AND u.role.name = :role
    """)
    Long countByUserRoleAndBranchId (@Param("role") String role,
                                     @Param("branchId") Long branchId);
}
