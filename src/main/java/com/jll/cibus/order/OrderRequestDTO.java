package com.jll.cibus.order;

import com.jll.cibus.orderdetail.OrderDetailRequestDTO;
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

    private Long branchId;

    private Long tableId;

    private Long waiterId;

    private Long orderStatusId;

    private List<OrderDetailRequestDTO> orders;
}
