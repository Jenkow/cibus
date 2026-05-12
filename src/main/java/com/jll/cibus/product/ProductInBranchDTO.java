package com.jll.cibus.product;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInBranchDTO {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Boolean available;
    private String description;
    private String categoryName;
}
