package com.jll.cibus.order.dto;

import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequestDTO {


    @NotNull(message = "Table is required")
    private Long tableId;

    @NotNull(message = "Waiter is required")
    private Long userId;

    @Valid
    @NotEmpty(message = "At least one item required")
    private List<OrderDetailRequestDTO> items;
}
