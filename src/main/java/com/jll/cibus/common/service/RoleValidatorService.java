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

    public boolean isAdmin(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("ID", id));

        return user.getRole().getName().equalsIgnoreCase("ADMIN");
    }

    public boolean isWaiter(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("ID", id));

        return user.getRole().getName().equalsIgnoreCase("WAITER");
    }

    public boolean isHost(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("ID", id));

        return user.getRole().getName().equalsIgnoreCase("HOST");
    }

    public boolean isKitchen(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("ID", id));

        return user.getRole().getName().equalsIgnoreCase("KITCHEN");
    }
}
