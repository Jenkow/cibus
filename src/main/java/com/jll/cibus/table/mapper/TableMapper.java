package com.jll.cibus.table.mapper;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.table.dto.TableCreateDTO;
import com.jll.cibus.table.dto.TableUpdateDTO;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;


    public TableEntity toEntity(TableUpdateDTO dto, Long branchId) {
        return modelMapper.map(dto, TableEntity.class);
    }

    public TableEntity toEntity(TableCreateDTO dto, Long branchId) {
        return modelMapper.map(dto, TableEntity.class);
    }

    public TableResponseDTO toResponse(TableEntity entity) {
        TableResponseDTO dto = modelMapper.map(entity, TableResponseDTO.class);
        dto.setBranchId(entity.getBranch().getId());
        if(entity.getWaiter()!= null){
            dto.setWaiterId(entity.getWaiter().getId());
        }
        return dto;
    }
}
