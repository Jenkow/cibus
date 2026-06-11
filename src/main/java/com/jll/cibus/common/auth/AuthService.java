package com.jll.cibus.common.auth;

import com.jll.cibus.common.config.JwtService;
import com.jll.cibus.common.exception.InvalidCredentialsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.role.CredentialsEntity;
import com.jll.cibus.role.CredentialsRepository;
import jakarta.transaction.Transactional;
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
    private final JwtService jwtService;

    public AuthResponse authenticate(AuthRequest input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
        CredentialsEntity user = credentialsRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        credentialsRepository.save(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        CredentialsEntity user = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new InvalidCredentialsException();
        }
        if (!jwtService.validateRefreshToken(refreshToken, user)) {
            throw new InvalidCredentialsException();
        }
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        credentialsRepository.save(user);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }


}
