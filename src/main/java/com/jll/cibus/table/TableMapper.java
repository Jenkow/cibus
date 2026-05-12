package com.jll.cibus.table;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.branch.BranchRepository;
import com.jll.cibus.user.UserEntity;
import com.jll.cibus.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TableMapper
{
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BranchRepository branchRepository;


    public TableEntity toEntity (TableRequestDTO dto, BranchEntity branch)
    {
        TableEntity entity = modelMapper.map(dto, TableEntity.class);

        entity.setBranch(branch);
        entity.setAvailable(true);
        entity.setWaiter(null);

        return entity;
    }

    public TableResponseDTO toResponse (TableEntity entity)
    {
        return modelMapper.map(entity, TableResponseDTO.class);
    }
}
