package com.jll.cibus.orderdetail.repository;

import com.jll.cibus.orderdetail.entity.OrderDetailEntity;
import com.jll.cibus.statistics.dto.product.ProductCategoryMetricDTO;
import com.jll.cibus.statistics.dto.product.ProductMetricDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    List<OrderDetailEntity> findByOrderId(Long orderId);

    Optional<OrderDetailEntity> findByOrderIdAndProductId(Long orderId, Long productId);

    // QUERY's DE ESTADISTICAS DE SUCURSAL
    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.branch.id = :branchId
        AND o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY SUM(od.quantity) DESC
    """)
    List<ProductMetricDTO> findMostPopularProducts(Long branchId, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.branch.id = :branchId
        AND o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY SUM(od.quantity) ASC
    """)
    List<ProductMetricDTO> findLessPopularProducts(Long branchId, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.branch.id = :branchId
        AND o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY od.unitPrice DESC
    """)
    List<ProductMetricDTO> findMostExpensiveProducts(Long branchId, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.branch.id = :branchId
        AND o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY od.unitPrice ASC
    """)
    List<ProductMetricDTO> findCheapestProducts(Long branchId, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductCategoryMetricDTO(od.product.category.name, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.branch.id = :branchId
        AND o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.category.name
        ORDER BY SUM(od.quantity) DESC
    """)
    List<ProductCategoryMetricDTO> findMostPopularCategories(Long branchId, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductCategoryMetricDTO(od.product.category.name, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.branch.id = :branchId
        AND o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.category.name
        ORDER BY SUM(od.quantity) ASC
    """)
    List<ProductCategoryMetricDTO> findLessPopularCategories(Long branchId, LocalDateTime start, LocalDateTime end);


    // QUERY's DE ESTADISTICAS GLOBALES
    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY SUM(od.quantity) DESC
    """)
    List<ProductMetricDTO> findGlobalMostPopularProducts(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY SUM(od.quantity) ASC
    """)
    List<ProductMetricDTO> findGlobalLessPopularProducts(LocalDateTime start, LocalDateTime end);


    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY od.unitPrice DESC
    """)
    List<ProductMetricDTO> findGlobalMostExpensiveProducts(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductMetricDTO(od.product.id, od.product.name, od.unitPrice, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.id, od.product.name, od.unitPrice
        ORDER BY od.unitPrice ASC
    """)
    List<ProductMetricDTO> findGlobalCheapestProducts(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductCategoryMetricDTO(od.product.category.name, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.category.name
        ORDER BY SUM(od.quantity) DESC
    """)
    List<ProductCategoryMetricDTO> findGlobalMostPopularCategories(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.jll.cibus.statistics.dto.product.ProductCategoryMetricDTO(od.product.category.name, SUM(od.quantity))
        FROM OrderDetailEntity od
        JOIN od.order o
        WHERE o.closedAt BETWEEN :start AND :end
        GROUP BY od.product.category.name
        ORDER BY SUM(od.quantity) ASC
    """)
    List<ProductCategoryMetricDTO> findGlobalLessPopularCategories(LocalDateTime start, LocalDateTime end);

}
