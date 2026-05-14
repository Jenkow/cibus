package com.jll.cibus.order.repository;

import com.jll.cibus.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>{

    List<OrderEntity> findByBranchId(Long branchId);

    List<OrderEntity> findByTableId(Long tableId);

    List<OrderEntity> findByWaiterId(Long waiterId);

    List<OrderEntity> findByBranch_IdAndStatus_Name(Long branchId, String statusName);

    List<OrderEntity> findByPaid(boolean paid);

}
