package com.jll.cibus.orderdetail.mapper;

import com.jll.cibus.common.model.IMapper;
import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailResponseDTO;
import com.jll.cibus.orderdetail.entity.OrderDetailEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
