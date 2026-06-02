package com.jll.cibus.order.repository;
import com.jll.cibus.order.entity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, Long> {

    List<OrderStatusEntity> findByName(String name);

}
