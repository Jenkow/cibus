package com.jll.cibus.common.service;

import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleValidatorService {
    private final UserRepository userRepository;

    public RoleValidatorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAdmin(Long dni){
        UserEntity user = userRepository.findByDni(dni)
                .orElseThrow( () -> new ResourceNotFoundException("DNI", dni));

        return user.getRole().getName().equalsIgnoreCase("ADMIN");
    }

    public boolean isWaiter(Long dni){
        UserEntity user = userRepository.findByDni(dni)
                .orElseThrow( () -> new ResourceNotFoundException("DNI", dni));

        return user.getRole().getName().equalsIgnoreCase("WAITER");
    }

    public boolean isHost(Long dni){
        UserEntity user = userRepository.findByDni(dni)
                .orElseThrow( () -> new ResourceNotFoundException("DNI", dni));

        return user.getRole().getName().equalsIgnoreCase("HOST");
    }

    public boolean isKitchen(Long dni){
        UserEntity user = userRepository.findByDni(dni)
                .orElseThrow( () -> new ResourceNotFoundException("DNI", dni));

        return user.getRole().getName().equalsIgnoreCase("KITCHEN");
    }
}
