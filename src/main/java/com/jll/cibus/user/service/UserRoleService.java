package com.jll.cibus.user;

import com.jll.cibus.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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

}
