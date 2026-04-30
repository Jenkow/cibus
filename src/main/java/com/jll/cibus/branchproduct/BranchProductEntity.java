package com.jll.cibus.branchproduct;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    @Column(name = "price", nullable = false, precision=10, scale=2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}
