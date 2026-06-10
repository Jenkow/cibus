package com.jll.cibus.order.repository;

import com.jll.cibus.order.entity.OrderEntity;
import org.hibernate.query.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    List<OrderEntity> findByBranchId(Long branchId);
    List<OrderEntity> findByBranchIdAndTableId (Long branchId, Long tableId);
    List<OrderEntity> findByBranchIdAndWaiterId (Long branchId, Long waiterId);
    List<OrderEntity> findByBranch_IdAndStatus_Name(Long branchId, String statusName);

}
