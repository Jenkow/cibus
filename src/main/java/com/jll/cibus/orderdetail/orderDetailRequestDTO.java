package com.jll.cibus.orderdetail;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class orderDetailRequestDTO
{
    @NotNull(message = "Product is mandatory")
    private Long productId;

    private String observation;

    @Min(value = 1)
    @NotNull
    private Integer quantity=1;

}
