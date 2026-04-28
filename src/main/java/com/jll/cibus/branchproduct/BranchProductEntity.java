package com.jll.cibus.branchproduct;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branch_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "price", nullable = false)               // Si usamos BigDecimal podemos poner precision=10, scale=2
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
