package com.jll.cibus.payment.repository;

import com.jll.cibus.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    public Optional<PaymentEntity> findByName(String name);

    public List<PaymentEntity> findByNameContaining(String name);

    public boolean existsByName(String name);
}
