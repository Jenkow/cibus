package com.jll.cibus.orderdetail;

import com.jll.cibus.order.OrderEntity;
import com.jll.cibus.product.ProductEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "observation")
    private String observation;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @DecimalMin("0.0")
    @Column(name = "unit_price", nullable = false, precision=10, scale=2)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",  nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
