package com.jll.cibus.order;

import com.jll.cibus.common.model.IMapper;
import com.jll.cibus.orderdetail.OrderDetailMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper implements IMapper<OrderEntity, OrderRequestDTO, OrderResponseDTO> {

    private final ModelMapper modelMapper;

    @Override
    public OrderResponseDTO toDTO(OrderEntity entity){
        OrderResponseDTO dto = modelMapper.map(entity, OrderResponseDTO.class);
        dto.setTableId(entity.getTable().getId());
        dto.setWaiterId(entity.getWaiter().getId());
        dto.setWaiterFirstName(entity.getWaiter().getFirstName());
        dto.setWaiterLastName(entity.getWaiter().getLastName());
        dto.setBranchId(entity.getBranch().getId());
        dto.setBranchName(entity.getBranch().getName());
        dto.setStatus(entity.getStatus().getName());
        return dto;
    }

    @Override
    public OrderEntity toEntity(OrderRequestDTO dto){
        return modelMapper.map(dto, OrderEntity.class);
    }

}
