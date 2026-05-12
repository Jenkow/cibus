package com.jll.cibus.orderdetail;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    List<OrderDetailEntity> findByOrderId(Long orderId);

    Optional<OrderDetailEntity> findByOrderIdAndProductId(Long orderId, Long productId);

}
