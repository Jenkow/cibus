package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.role.Roles;
import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.dto.UserUpdateDTO;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.mapper.UserMapper;
import com.jll.cibus.user.repository.UserRepository;
import com.jll.cibus.user.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BranchService branchService;

    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (existsByDni(requestDTO.getDni())) throw new ResourceAlreadyExistsException("User", requestDTO.getDni());
        if (userRepository.existsByEmail(requestDTO.getEmail())) throw new ResourceAlreadyExistsException("Email", requestDTO.getEmail());

        BranchEntity branch = branchService.getEntity(requestDTO.getBranchId());

        UserEntity user = userMapper.toEntity(requestDTO);
        user.setBranch(branch);
        UserEntity created = userRepository.save(user);
        return userMapper.toResponse(created);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO updateDTO) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User ID", id));
        if (updateDTO.getDni() != null) {
            user.setDni(updateDTO.getDni());
        }
        if (updateDTO.getFirstName() != null && !updateDTO.getFirstName().isBlank()) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null && !updateDTO.getLastName().isBlank()) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getBranchId() != null) {
            BranchEntity branch = branchService.getEntity(updateDTO.getBranchId());
            user.setBranch(branch);
        }
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    public void delete(Long id) {
        UserEntity toDelete = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User ID", id));

        userRepository.delete(toDelete);
    }

    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    public Page<UserResponseDTO> findAll(Pageable pageable, Long dni, String name, String email, String phoneNumber, Long branchId, Long userRoleId) {
        Specification<UserEntity> spec = Specification.allOf(
                UserSpecification.nameContains(name),
                UserSpecification.dniEquals(dni),
                UserSpecification.emailEquals(email),
                UserSpecification.phoneNumberEquals(phoneNumber),
                UserSpecification.branchIdEquals(branchId),
                UserSpecification.userRoleIdEquals(userRoleId)
        );
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toResponse);
    }

    public UserResponseDTO findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User ID", id));
        return userMapper.toResponse(user);
    }

    public boolean existsByDni(Long dni) {
        return userRepository.existsByDni(dni);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public boolean existsByDniAndBranch(Long dni, Long branchId) {
        return userRepository.existsByDniAndBranchId(dni, branchId);
    }

    public UserEntity getEntityByDni(Long dni) {
        return userRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("DNI", dni));
    }

    public UserEntity getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID", id));
    }

    public List<String> getUserRoles() {
        return Arrays.stream(Roles.values())
                .map(Roles::name)
                .toList();
    }

}
