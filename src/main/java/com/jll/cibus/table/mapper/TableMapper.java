package com.jll.cibus.table.mapper;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.table.dto.TableCreateDTO;
import com.jll.cibus.table.dto.TableUpdateDTO;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TableMapper {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BranchRepository branchRepository;


    public TableEntity toEntity(TableUpdateDTO dto, Long branchId) {
        return modelMapper.map(dto, TableEntity.class);
    }

    public TableEntity toEntity(TableCreateDTO dto, Long branchId) {
        return modelMapper.map(dto, TableEntity.class);
    }

    public TableResponseDTO toResponse(TableEntity entity) {
        return modelMapper.map(entity, TableResponseDTO.class);
    }
}
