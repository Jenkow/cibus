package com.jll.cibus.common.auth;

import com.jll.cibus.common.config.JwtService;
import com.jll.cibus.role.CredentialsEntity;
import com.jll.cibus.user.dto.UserRequestDTO;
import com.jll.cibus.user.dto.UserResponseDTO;
import com.jll.cibus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping()
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest){
        CredentialsEntity user = authService.authenticate(authRequest);
        System.out.println(user);
        String token = jwtService.generateToken(user);
        System.out.println(token);
        return ResponseEntity.ok(new AuthResponse(token,
                user.getRefreshToken()));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        AuthResponse response =
                authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
        }


//    @PostMapping("/register")
//    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO newUser){
//        return new ResponseEntity<>(userService.create(newUser),
//                HttpStatus.CREATED);
//    }
}
