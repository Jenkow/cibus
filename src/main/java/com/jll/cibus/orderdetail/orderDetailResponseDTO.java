package com.jll.cibus.orderdetail;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class orderDetailResponseDTO
{
    private Long id;
    private Long orderId;
    private String observation;
    private Integer quantity;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;

    // We could add a subtotal function in service and add that atribute here

}
