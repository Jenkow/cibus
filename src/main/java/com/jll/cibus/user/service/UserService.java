package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.role.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BranchService branchService;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (existsByDni(requestDTO.getDni())) throw new ResourceAlreadyExistsException("User", requestDTO.getDni());
        if (userRepository.existsByEmail(requestDTO.getEmail())) throw new ResourceAlreadyExistsException("Email", requestDTO.getEmail());
        UserEntity user = userMapper.toEntity(requestDTO);
        BranchEntity branch = branchService.getEntity(requestDTO.getBranchId());
        user.setBranch(branch);
        Roles role = Roles.valueOf(requestDTO.getRole().toUpperCase());
        RoleEntity roleEntity = roleRepository.findByRole(role)
                .orElseThrow(() -> new ResourceNotFoundException("role", requestDTO.getRole()));
        String cleanPhone = user.getPhoneNumber().replaceAll("\\D", "");             // saca todos los caracteres que no sean numeros
        String pin = cleanPhone.substring(cleanPhone.length() - 6);                         // se queda con los ulitmos 6
        CredentialsEntity credentials = CredentialsEntity.builder()
                .user(user)
                .roles(new HashSet<>(Set.of(roleEntity)))
                .build();
        if (role == Roles.ADMIN || role == Roles.MANAGER) {
            credentials.setUsername(user.getEmail());
        } else {
            credentials.setUsername(pin);
        }
        credentials.setPassword(passwordEncoder.encode(pin));
        credentialsRepository.save(credentials);
        UserEntity created = userRepository.save(user);
        return userMapper.toResponse(created);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO updateDTO) {
        UserEntity user = getEntityById(id);
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
        UserEntity user = getEntityById(id);
        userRepository.delete(user);
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
