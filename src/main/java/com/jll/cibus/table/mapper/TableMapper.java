package com.jll.cibus.table.mapper;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.table.dto.TableRequestDTO;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.repository.UserRepository;
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
