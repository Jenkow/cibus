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


    public TableEntity toEntity (TableRequestDTO dto, Long branchId)
    {
        TableEntity entity = modelMapper.map(dto, TableEntity.class);
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(()->new RuntimeException("Branch not found"));
        entity.setBranch(branch); if (dto.getWaiterId()!=null) {
            UserEntity waiter = userRepository.findById(dto.getWaiterId())
                    .orElseThrow(() -> new RuntimeException("Waiter not found"));
            entity.setWaiter(waiter);
        }
        return entity;
    }

    public TableResponseDTO toResponse (TableEntity entity)
    {
        return modelMapper.map(entity, TableResponseDTO.class);
    }
}
