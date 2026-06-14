package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.common.exception.*;
import com.jll.cibus.credential.entity.CredentialsEntity;
import com.jll.cibus.credential.repository.CredentialsRepository;
import com.jll.cibus.role.entity.RoleEntity;
import com.jll.cibus.role.enums.Roles;
import com.jll.cibus.role.repository.RoleRepository;
import com.jll.cibus.user.dto.ChangePasswordRequestDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;


    private UserEntity getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedOperationException("User not authenticated");
        }
        String username = (String) authentication.getPrincipal();
        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Credentials not found"));
        UserEntity user = credentials.getUser();
        if(user == null){
            throw new ResourceNotFoundException("There is no user related to credentials");
        }
        return user;
    }

    private void authenticateUserBelongsInBranch(Long branchId){
        UserEntity user = getAuthenticatedUser();
        if(user.getRole().getRole() != Roles.ADMIN){
            if(!user.getBranch().getId().equals(branchId)){
                throw new BusinessException("Waiter assigned to a different branch");
            }
        }
    }

    public Page<UserResponseDTO> findAll(Pageable pageable, Long dni, String name, String email, String phoneNumber, Long branchId, Long userRoleId) {
        UserEntity user = getAuthenticatedUser();
        if(user.getRole().getRole() == Roles.MANAGER){
            branchId = user.getBranch().getId();
        }
        Specification<UserEntity> spec = Specification.allOf(
                UserSpecification.nameContains(name),
                UserSpecification.dniEquals(dni),
                UserSpecification.emailEquals(email),
                UserSpecification.phoneNumberEquals(phoneNumber),
                UserSpecification.branchIdEquals(branchId),
                UserSpecification.userRoleIdEquals(userRoleId)
        );
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toDTO);
    }

    public UserResponseDTO findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User ID", id));
        return userMapper.toDTO(user);
    }

    private UserEntity getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID", id));
    }

    private UserEntity getEntityByDni(Long dni) {
        return userRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("DNI", dni));
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

    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (existsByDni(requestDTO.getDni())) throw new ResourceAlreadyExistsException("User", requestDTO.getDni());
        if (userRepository.existsByEmail(requestDTO.getEmail())) throw new ResourceAlreadyExistsException("Email", requestDTO.getEmail());

        UserEntity user = userMapper.toEntity(requestDTO);
        if(requestDTO.getBranchId() != null){
            BranchEntity branch = branchRepository.findById(requestDTO.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("branch", requestDTO.getBranchId()));
            user.setBranch(branch);
        }
        Roles role = Roles.valueOf(requestDTO.getRole().toUpperCase());
        RoleEntity roleEntity = roleRepository.findByRole(role)
                .orElseThrow(() -> new ResourceNotFoundException("role", requestDTO.getRole()));
        user.setRole(roleEntity);
        String pin = requestDTO.getPin();

        CredentialsEntity credentials = CredentialsEntity.builder()
                .enabled(Boolean.TRUE)
                .user(user)
                .roles(new HashSet<>(Set.of(roleEntity)))
                .build();
        if (role == Roles.ADMIN || role == Roles.MANAGER) {
            credentials.setUsername(user.getEmail());
        } else {
            if (credentialsRepository.existsByUsername(pin))
            {
                throw  new ResourceAlreadyExistsException("PIN", pin);
            }
            credentials.setUsername(pin);
        }
        credentials.setPassword(passwordEncoder.encode(pin));
        credentialsRepository.save(credentials);
        UserEntity created = userRepository.save(user);
        return userMapper.toDTO(created);
    }

    @Transactional
    public UserResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request){
        UserEntity user = getEntityById(userId);
        UserEntity authenticatedUser = getAuthenticatedUser();
        if(authenticatedUser.getRole().getRole() == Roles.MANAGER){
            if(authenticatedUser.getBranch() == null || user.getBranch() == null){
                throw new BusinessException("User must be assigned to a branch");
            }
            if(!authenticatedUser.getBranch().getId().equals(user.getBranch().getId())){
                throw new BusinessException("You can't modify users belonging to a different branch.");
            }
        }
        CredentialsEntity credentials = credentialsRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Credentials not found"));
        if(!passwordEncoder.matches(request.getCurrentPassword(), credentials.getPassword())){
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        if(user.getRole().getRole() != Roles.ADMIN && user.getRole().getRole() != Roles.MANAGER){
            if (!request.getNewPassword().matches("\\d{4}")) {
                throw new InvalidCredentialsException("Password must be exactly 4 numeric digits");
            }
            credentials.setUsername(request.getNewPassword());
        }
        credentials.setPassword(passwordEncoder.encode(request.getNewPassword()));
        credentialsRepository.save(credentials);
        return userMapper.toDTO(user);
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
            BranchEntity branch = branchRepository.findById(updateDTO.getBranchId())
                    .orElseThrow(()-> new ResourceNotFoundException("branch", updateDTO.getBranchId()));
            user.setBranch(branch);
        }
        if(updateDTO.getRole()!=null && !updateDTO.getRole().isBlank()){
            Roles role = Roles.valueOf(updateDTO.getRole().toUpperCase());
            RoleEntity roleEntity = roleRepository.findByRole(role)
                    .orElseThrow(() -> new ResourceNotFoundException("role", role.name()));
            user.setRole(roleEntity);
        }
        UserEntity updated = userRepository.save(user);
        return userMapper.toDTO(updated);
    }

    public void delete(Long id) {
        UserEntity user = getEntityById(id);
        CredentialsEntity credentials = credentialsRepository.findByUser_Id(id)
                        .orElseThrow(() -> new ResourceNotFoundException("There are no credentials for user "+id));
        credentials.disable();
        user.setBranch(null);
        credentialsRepository.save(credentials);
        userRepository.save(user);
    }

    public List<String> getUserRoles() {
        return Arrays.stream(Roles.values())
                .map(Roles::name)
                .toList();
    }

}
