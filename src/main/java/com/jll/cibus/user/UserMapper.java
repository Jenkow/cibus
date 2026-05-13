package com.jll.cibus.user;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.branch.BranchRepository;
import com.jll.cibus.user.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
