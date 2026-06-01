package com.jll.cibus.user.service;

import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.user.entity.UserRoleEntity;
import com.jll.cibus.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public List<UserRoleEntity> getAll()
    {
        return userRoleRepository.findAll();
    }

    private void  idVerification (Long id) {
        if (!userRoleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
    }

    public UserRoleEntity getEntity (Long id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }
}
