package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
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
import org.springframework.data.support.PageableExecutionUtils;
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

    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (existsByDni(requestDTO.getDni())) throw new ResourceAlreadyExistsException("User", requestDTO.getDni());
        if (userRepository.existsByEmail(requestDTO.getEmail())) throw new ResourceAlreadyExistsException("Email", requestDTO.getEmail());

        BranchEntity branch = branchService.getEntity(requestDTO.getBranchId());
        UserRoleEntity userRole = userRoleService.getEntity(requestDTO.getUserRoleId());

        UserEntity user = userMapper.toEntity(requestDTO);
        user.setBranch(branch);
        user.setRole(userRole);
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
        if (updateDTO.getUserRoleId() != null) {
            UserRoleEntity userRole = userRoleService.getEntity(updateDTO.getUserRoleId());
            user.setRole(userRole);
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

    public UserResponseDTO findByDni(Long dni) {
        UserEntity user = userRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("User DNI", dni));

        return userMapper.toResponse(user);
    }

    public List<UserResponseDTO> findByNameContaining(String name) {
        List<UserEntity> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);

        if (users.isEmpty()) throw new ResourceNotFoundException("User name", name);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByFirstName(String firstName) {
        List<UserEntity> users = userRepository.findByFirstName(firstName);

        if (users.isEmpty()) throw new ResourceNotFoundException("User First Name", firstName);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByLastName(String lastName) {
        List<UserEntity> users = userRepository.findByLastName(lastName);

        if (users.isEmpty()) throw new ResourceNotFoundException("User Last name", lastName);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponseDTO findByEmail(String email) {
        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User email", email));

        return userMapper.toResponse(user);
    }

    public UserResponseDTO findByPhoneNumber(String phoneNumber) {
        UserEntity user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User phone number", phoneNumber));

        return userMapper.toResponse(user);
    }

    public List<UserResponseDTO> findByFirstNameAndLastName(String firstName, String lastName) {
        List<UserEntity> users = userRepository.findByFirstNameAndLastName(firstName, lastName);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User first  last name", firstName + " and/or last name " + lastName);
        }

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByBranchId(Long branchId) {
        List<UserEntity> users = userRepository.findByBranchId(branchId);

        if (users.isEmpty()) throw new ResourceNotFoundException("Branch", branchId);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByRoleId(Long roleId) {
        List<UserEntity> users = userRepository.findByRoleId(roleId);

        if (users.isEmpty()) throw new ResourceNotFoundException("Role", roleId);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
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
