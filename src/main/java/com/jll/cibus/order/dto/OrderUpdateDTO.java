package com.jll.cibus.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderUpdateDTO {

    @Positive
    private Integer tableNumber;

    @Positive
    private Long statusId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closedAt;

    @PositiveOrZero
    private BigDecimal subtotal;

    @PositiveOrZero
    private BigDecimal discount;

    @PositiveOrZero
    private BigDecimal finalTotal;

}
