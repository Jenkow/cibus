package com.jll.cibus.user;

import com.jll.cibus.branch.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>
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

    List<UserEntity> findByUserRole_Id (Long userRoleId);

}
