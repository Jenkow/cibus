package com.jll.cibus.common.auth;

import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.role.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;

    public UserDetails authenticate (AuthRequest input)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        return credentialsRepository.findByUsername(input.getUsername())
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

    }
}
