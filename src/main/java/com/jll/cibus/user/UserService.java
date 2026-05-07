package com.jll.cibus.user;

import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO create (UserRequestDTO requestDTO) {
        if (userRepository.findByDni(requestDTO.getDni()).isPresent()) throw new RuntimeException("DNI not found"); //REFACTOR TO UserNotFoundException PENDING


    }

    //... PENDING TO FINISH THE CRUD
}
