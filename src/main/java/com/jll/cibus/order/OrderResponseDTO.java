package com.jll.cibus.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jll.cibus.orderdetail.OrderDetailResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderResponseDTO {
    private Long id;
    private Long tableId;
    private Long waiterId;
    private String waiterName;
    private Long branchId;
    private String branchName;
    private Boolean paid;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private BigDecimal total;
    private List<OrderDetailResponseDTO> items;
}
