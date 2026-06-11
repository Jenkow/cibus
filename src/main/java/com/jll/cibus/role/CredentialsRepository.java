package com.jll.cibus.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<CredentialsEntity, Long> {

    boolean existsByUsername(String username);

    Optional<CredentialsEntity> findByUsername(String username);

    Optional<CredentialsEntity> findByUser_Id(Long usuarioId);
}
