package com.jll.cibus.orderdetail;

import com.jll.cibus.order.OrderEntity;
import com.jll.cibus.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "observation",  nullable = true)
    private String observation;

    @Column(name = "amount") // no es nullable porque por defecto siempre va a tener valor 1 a menos que se indique lo contrario
    private Integer amount = 1;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",  nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
