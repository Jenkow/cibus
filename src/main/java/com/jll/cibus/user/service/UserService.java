package com.jll.cibus.user.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.entity.UserRoleEntity;
import com.jll.cibus.user.mapper.UserMapper;
import com.jll.cibus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (existsByDni(requestDTO.getDni())) throw new ResourceAlreadyExistsException("User", requestDTO.getDni());

        BranchEntity branch = branchService.getEntity(requestDTO.getBranchId());
        UserRoleEntity userRole = userRoleService.getEntity(requestDTO.getUserRoleId());

        UserEntity user = userMapper.toEntity(requestDTO);
        user.setBranch(branch);
        user.setRole(userRole);

        UserEntity created = userRepository.save(user);
        return userMapper.toResponse(created);
    }

    @Transactional
    public UserResponseDTO update(UserRequestDTO requestDTO) {
        UserEntity toUpdate = userRepository.findByDni(requestDTO.getDni())
                .orElseThrow(() -> new ResourceNotFoundException("User", requestDTO.getDni()));

        BranchEntity branch = branchService.getEntity(requestDTO.getBranchId());
        UserRoleEntity userRole = userRoleService.getEntity(requestDTO.getUserRoleId());

        toUpdate.setBranch(branch);
        toUpdate.setRole(userRole);
        toUpdate.setEmail(requestDTO.getEmail());
        toUpdate.setFirstName(requestDTO.getFirstName());
        toUpdate.setLastName(requestDTO.getLastName());

        UserEntity updated = userRepository.save(toUpdate);
        return userMapper.toResponse(updated);
    }

    public void deleteByDni(Long dni){
        UserEntity toDelete = userRepository
                .findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("User DNI", dni));

        userRepository.delete(toDelete);
    }

    public List<UserResponseDTO> findAll(){
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
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

    public List<UserResponseDTO> findByFirstName (String firstName){
        List<UserEntity> users = userRepository.findByFirstName(firstName);

        if (users.isEmpty()) throw new ResourceNotFoundException("User First Name", firstName);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByLastName (String lastName){
        List<UserEntity> users = userRepository.findByLastName(lastName);

        if(users.isEmpty()) throw new ResourceNotFoundException("User Last name", lastName);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponseDTO findByEmail (String email){
        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User email", email));

        return userMapper.toResponse(user);
    }

    public UserResponseDTO findByPhoneNumber (String phoneNumber){
        UserEntity user = userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User phone number", phoneNumber));

        return userMapper.toResponse(user);
    }

    public List<UserResponseDTO> findByFirstNameAndLastName (String firstName, String lastName){
        List<UserEntity> users = userRepository.findByFirstNameAndLastName(firstName, lastName);

        if(users.isEmpty()){
            throw new ResourceNotFoundException("User first  last name", firstName + " and/or last name " + lastName);
        }

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByBranchId (Long branchId) {
        List<UserEntity> users = userRepository.findByBranchId(branchId);

        if(users.isEmpty()) throw new ResourceNotFoundException("Branch", branchId);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public List<UserResponseDTO> findByRoleId (Long roleId) {
        List<UserEntity> users = userRepository.findByRoleId(roleId);

        if(users.isEmpty()) throw new ResourceNotFoundException("Role", roleId);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public boolean existsByDni(Long dni){
        return userRepository.existsByDni(dni);
    }

    public boolean existsByDniAndBranch(Long dni, Long branchId){
        return userRepository.existsByDniAndBranchId(dni, branchId);
    }

    public UserEntity getEntityByDni(Long dni) {
        return userRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("DNI", dni));
    }


}
