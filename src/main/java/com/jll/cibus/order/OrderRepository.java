package com.jll.cibus.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>{

    List<OrderEntity> findByBranchId(Long branchId);

    List<OrderEntity> findByTableId(Long tableId);

    List<OrderEntity> findByWaiterId(Long waiterId);

    List<OrderEntity> findByStatus(String status);

    List<OrderEntity> findByIsPaid(boolean isPaid);

}
