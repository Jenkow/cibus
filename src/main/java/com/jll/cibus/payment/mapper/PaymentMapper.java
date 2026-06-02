package com.jll.cibus.payment.mapper;

import com.jll.cibus.payment.dto.PaymentRequestDTO;
import com.jll.cibus.payment.dto.PaymentResponseDTO;
import com.jll.cibus.payment.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final ModelMapper modelMapper;

    public PaymentEntity toEntity(PaymentRequestDTO dto){
        return modelMapper.map(dto, PaymentEntity.class);
    }

    public PaymentResponseDTO toDTO(PaymentEntity entity){
        return modelMapper.map(entity, PaymentResponseDTO.class);
    }

}
