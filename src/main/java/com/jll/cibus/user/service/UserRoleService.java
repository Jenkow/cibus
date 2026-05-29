package com.jll.cibus.user.service;

import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.user.entity.UserRoleEntity;
import com.jll.cibus.user.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService
{
    @Autowired
    private UserRoleRepository userRoleRepository;

    public List<UserRoleEntity> getAllUserRol ()
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
