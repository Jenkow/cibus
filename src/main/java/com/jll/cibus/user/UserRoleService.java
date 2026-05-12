package com.jll.cibus.user;

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

    private void  idVerification (Long id)
    {
        if (!userRoleRepository.existsById(id))
        {
            throw new RuntimeException("THERES NO USER ROLE WITH ID "+id);
        }
    }

}
