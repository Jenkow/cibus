package com.jll.cibus.user.mapper;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.UserRoleEntity;
import com.jll.cibus.user.UserRoleRepository;
import com.jll.cibus.user.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private ModelMapper modelMapper;
    private BranchRepository branchRepository;
    private UserRoleRepository userRoleRepository;

    public UserMapper(ModelMapper modelMapper, BranchRepository branchRepository, UserRoleRepository userRoleRepository) {
        this.modelMapper = modelMapper;
        this.branchRepository = branchRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public UserEntity toEntity (UserRequestDTO dto)
    {
        UserEntity entity = modelMapper.map(dto, UserEntity.class);

        UserRoleEntity role = userRoleRepository.findById(dto.getUserRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        entity.setRole(role);

        if (dto.getBranchId()!= null)
        {
            BranchEntity branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            entity.setBranch(branch);
        }
        return entity;
    }

    public UserResponseDTO toResponse (UserEntity entity)
    {
        return modelMapper.map(entity, UserResponseDTO.class);
    }


}
