package com.jll.cibus.order;

import com.jll.cibus.orderdetail.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, Long> {

    List<OrderEntity> findByName(String name);

}
