package com.jll.cibus.branch;

import com.jll.cibus.orderdetail.BranchResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class BranchMapper {

    private final ModelMapper modelMapper;

    public BranchMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BranchEntity toEntity (BranchRequestDTO dto)
    {
        return modelMapper.map(dto, BranchEntity.class);
    }
    public BranchResponseDTO toResponseDTO (BranchEntity entity)
    {
        return modelMapper.map(entity, BranchResponseDTO.class);
    }

}