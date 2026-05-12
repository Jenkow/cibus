package com.jll.cibus.orderdetail;

import com.jll.cibus.common.model.IMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDetailMapper implements IMapper<OrderDetailEntity, OrderDetailRequestDTO, OrderDetailResponseDTO> {

    private final ModelMapper modelMapper;

    @Override
    public OrderDetailResponseDTO toDTO(OrderDetailEntity entity){
        OrderDetailResponseDTO dto = modelMapper.map(entity, OrderDetailResponseDTO.class);
        dto.setOrderId(entity.getOrder().getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setProductName(entity.getProduct().getName());
        return dto;
    }

    @Override
    public OrderDetailEntity toEntity(OrderDetailRequestDTO dto){
        return modelMapper.map(dto, OrderDetailEntity.class);
    }

}
