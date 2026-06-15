package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.common.exception.*;
import com.jll.cibus.credential.entity.CredentialsEntity;
import com.jll.cibus.credential.repository.CredentialsRepository;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.role.entity.RoleEntity;
import com.jll.cibus.role.enums.Roles;
import com.jll.cibus.role.repository.RoleRepository;
import com.jll.cibus.user.dto.*;
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
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final OrderRepository orderRepository;
    private final UserMapper userMapper;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;


    private UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedOperationException("User not authenticated");
        }
        String username = (String) authentication.getPrincipal();
        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Credentials not found"));
        UserEntity user = credentials.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("There is no user related to credentials");
        }
        return user;
    }

    public Page<UserResponseDTO> findAll(Pageable pageable, Long dni, String name, String email, String phoneNumber, Long branchId, Long userRoleId) {
        UserEntity user = getAuthenticatedUser();
        if (user.getRole().getRole() == Roles.MANAGER) {
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
                .orElseThrow(() -> new ResourceNotFoundException("User ID", id));
    }

    private UserEntity getEntityByDni(Long dni) {
        return userRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("DNI", dni));
    }

    private CredentialsEntity getCredentials(Long userId) {
        return credentialsRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Credentials not found"));
    }

    private RoleEntity getRole(String roleName) {
        Roles role = Roles.valueOf(roleName.toUpperCase());
        return roleRepository.findByRole(role)
                .orElseThrow(() -> new ResourceNotFoundException("role", roleName));
    }

    private BranchEntity getBranch(Long branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("branch", branchId));
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
        if (userRepository.existsByEmail(requestDTO.getEmail()))
            throw new ResourceAlreadyExistsException("Email", requestDTO.getEmail());
        UserEntity user = userMapper.toEntity(requestDTO);
        if (requestDTO.getBranchId() != null) {
            BranchEntity branch = getBranch(requestDTO.getBranchId());
            user.setBranch(branch);
        }
        RoleEntity roleEntity = getRole(requestDTO.getRole());
        user.setRole(roleEntity);
        String pin = requestDTO.getPin();
        CredentialsEntity credentials = CredentialsEntity.builder()
                .enabled(Boolean.TRUE)
                .user(user)
                .roles(new HashSet<>(Set.of(roleEntity)))
                .build();
        if (roleEntity.getRole() == Roles.ADMIN || roleEntity.getRole() == Roles.MANAGER) {
            credentials.setUsername(user.getEmail());
        } else {
            if (credentialsRepository.existsByUsername(pin)) {
                throw new ResourceAlreadyExistsException("PIN", pin);
            }
            credentials.setUsername(pin);
        }
        credentials.setPassword(passwordEncoder.encode(pin));
        credentialsRepository.save(credentials);
        UserEntity created = userRepository.save(user);
        return userMapper.toDTO(created);
    }

    private void validateManagerCanModifyUser(UserEntity authenticatedUser, UserEntity userToModify) {
        if (authenticatedUser.getBranch() == null || userToModify.getBranch() == null) {
            throw new BusinessException("User must be assigned to a branch.");
        }
        if (!authenticatedUser.getBranch().getId().equals(userToModify.getBranch().getId())) {
            throw new BusinessException("You can't modify users belonging to a different branch.");
        }
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO updateDTO) {
        UserEntity user = getEntityById(id);
        UserEntity authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole().getRole() == Roles.MANAGER) {
            validateManagerCanModifyUser(authenticatedUser, user);
        }
        if (updateDTO.getDni() != null && !updateDTO.getDni().equals(user.getDni())) {
            if (userRepository.existsByDni(updateDTO.getDni())) {
                throw new ResourceAlreadyExistsException("User", updateDTO.getDni());
            }
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
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank() && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new ResourceAlreadyExistsException("Email", updateDTO.getEmail());
            }
            CredentialsEntity credentials = getCredentials(id);
            if (user.getRole().getRole() == Roles.ADMIN || user.getRole().getRole() == Roles.MANAGER) {
                credentials.setUsername(updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
            credentialsRepository.save(credentials);
        }
        if (updateDTO.getBranchId() != null) {
            BranchEntity branch = getBranch(updateDTO.getBranchId());
            user.setBranch(branch);
        }
        UserEntity updated = userRepository.save(user);
        return userMapper.toDTO(updated);
    }

    @Transactional
    public UserResponseDTO changeRole(Long userId, ChangeRoleDTO updateDTO) {
        UserEntity user = getEntityById(userId);
        UserEntity authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole().getRole() == Roles.MANAGER) {
            validateManagerCanModifyUser(authenticatedUser, user);
        }
        CredentialsEntity credentials = getCredentials(userId);
        RoleEntity newRole = getRole(updateDTO.getRoleName());
        if (user.getRole().getRole() == newRole.getRole()) {
            return userMapper.toDTO(user);
        }
        boolean oldUsesEmailCredentials = user.getRole().getRole() == Roles.ADMIN || user.getRole().getRole() == Roles.MANAGER;
        boolean newUsesEmailCredentials = newRole.getRole() == Roles.ADMIN || newRole.getRole() == Roles.MANAGER;
        if (oldUsesEmailCredentials != newUsesEmailCredentials) {
            if (newUsesEmailCredentials) {
                credentials.setUsername(user.getEmail());
            } else {
                if(updateDTO.getNewPin() == null){
                    throw new BusinessException("New pin is mandatory for this role transition");
                }
                credentials.setUsername(updateDTO.getNewPin());
                credentials.setPassword(passwordEncoder.encode(updateDTO.getNewPin()));
            }
        }
        user.setRole(newRole);
        credentials.getRoles().clear();
        credentials.getRoles().add(newRole);
        UserEntity updated = userRepository.save(user);
        credentialsRepository.save(credentials);
        return userMapper.toDTO(updated);
    }

    @Transactional
    public UserResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request) {
        UserEntity user = getEntityById(userId);
        UserEntity authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole().getRole() == Roles.MANAGER) {
            validateManagerCanModifyUser(authenticatedUser, user);
        }
        CredentialsEntity credentials = getCredentials(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), credentials.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        if (user.getRole().getRole() != Roles.ADMIN && user.getRole().getRole() != Roles.MANAGER) {
            if (!request.getNewPassword().matches("\\d{4}")) {
                throw new InvalidCredentialsException("Password must be exactly 4 numeric digits");
            }
            if (credentialsRepository.existsByUsername(request.getNewPassword())) {
                throw new ResourceAlreadyExistsException("PIN", request.getNewPassword());
            }
            credentials.setUsername(request.getNewPassword());
        }
        credentials.setPassword(passwordEncoder.encode(request.getNewPassword()));
        credentialsRepository.save(credentials);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponseDTO resetPin(Long userId, String newPin) {
        CredentialsEntity credentials = getCredentials(userId);
        UserEntity user = credentials.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("There is no user related to credentials");
        }
        UserEntity authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole().getRole() == Roles.MANAGER) {
            validateManagerCanModifyUser(authenticatedUser, user);
        }
        Roles role = user.getRole().getRole();
        if (role != Roles.ADMIN && role != Roles.MANAGER) {
            if (credentialsRepository.existsByUsername(newPin)) {
                throw new ResourceAlreadyExistsException("PIN", newPin);
            }
            credentials.setUsername(newPin);
        }
        credentials.setPassword(passwordEncoder.encode(newPin));
        credentialsRepository.save(credentials);
        return userMapper.toDTO(user);
    }

    public void delete(Long id) {
        UserEntity user = getEntityById(id);
        CredentialsEntity credentials = credentialsRepository.findByUser_Id(id)
                        .orElseThrow(() -> new ResourceNotFoundException("There are no credentials for user "+id));

        if(user.getRole().getRole() == Roles.WAITER
                || user.getRole().getRole() == Roles.MANAGER
                || user.getRole().getRole() == Roles.HOST){

            if(orderRepository.userHasOrdersActive(user.getId())){
                throw new BusinessException("Cannot delete user that has active orders.");
            }
        }
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
