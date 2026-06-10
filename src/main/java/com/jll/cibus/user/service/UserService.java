package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.user.dto.ChangePinDTO;
import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.dto.UserUpdateDTO;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.entity.UserRoleEntity;
import com.jll.cibus.user.mapper.UserMapper;
import com.jll.cibus.user.repository.UserRepository;
import com.jll.cibus.user.repository.UserRoleRepository;
import com.jll.cibus.user.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BranchService branchService;
    private final UserRoleService userRoleService;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (existsByDni(requestDTO.getDni())) throw new ResourceAlreadyExistsException("User", requestDTO.getDni());
        if (userRepository.existsByEmail(requestDTO.getEmail())) throw new ResourceAlreadyExistsException("Email", requestDTO.getEmail());
        BranchEntity branch = branchService.getEntity(requestDTO.getBranchId());
        UserRoleEntity userRole = userRoleService.getEntity(requestDTO.getUserRoleId());
        UserEntity user = userMapper.toEntity(requestDTO);
        user.setBranch(branch);
        user.setRole(userRole);
        user.setPin(user.getPhoneNumber().substring(user.getPhoneNumber().length() - 6));
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
        if (updateDTO.getUserRoleId() != null) {
            UserRoleEntity userRole = userRoleService.getEntity(updateDTO.getUserRoleId());
            user.setRole(userRole);
        }
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public UserResponseDTO changePin(Long id, ChangePinDTO newPin){      //crear endpoint en auth: /api/auth/change-password
        UserEntity user = getEntityById(id);                             //esto va a recibir el auth y de ahi saca el usuario, no recibira el id.
        user.setPin(passwordEncoder.encode(newPin.getPin()));
        UserEntity saved = userRepository.save(user);
        return userMapper.toResponse(saved);
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

    public List<String> getUserRoles(){
        return userRoleRepository.findAll().stream()
                .map(UserRoleEntity::getName)
                .toList();
    }

}
