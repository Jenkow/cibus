package com.jll.cibus.order.repository;

import com.jll.cibus.order.entity.OrderEntity;
import com.jll.cibus.statistics.dto.table.TableMetricDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    List<OrderEntity> findByBranchId(Long branchId);
    List<OrderEntity> findByBranchIdAndTableId (Long branchId, Long tableId);
    List<OrderEntity> findByBranchIdAndWaiterId (Long branchId, Long waiterId);
    List<OrderEntity> findByBranch_IdAndStatus_Name(Long branchId, String statusName);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.table.TableMetricDTO(t.number, COUNT(o))
    FROM OrderEntity o
    JOIN o.table t
    WHERE o.branch.id = :branchId
    AND o.createdAt BETWEEN :start AND :end
    GROUP BY t.id, t.number
    ORDER BY COUNT(o) DESC
    """)
    List<TableMetricDTO> findMostBusyTable(Long branchId, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.table.TableMetricDTO(t.number, COUNT(o))
    FROM OrderEntity o
    JOIN o.table t
    WHERE o.branch.id = :branchId
    AND o.createdAt BETWEEN :start AND :end
    GROUP BY t.id, t.number
    ORDER BY COUNT(o) ASC
    """)
    List<TableMetricDTO> findLessBusyTable(Long branchId, LocalDateTime start, LocalDateTime end);



    @Query("""
    SELECT COUNT(DISTINCT o.table.id)
    FROM OrderEntity o
    WHERE o.branch.id = :branchId
    AND o.createdAt BETWEEN :start AND :end
    """)
    Long countOccupiedTables(Long branchId, LocalDateTime start, LocalDateTime end);

}
