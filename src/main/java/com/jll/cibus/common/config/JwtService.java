//package com.jll.cibus.common.config;
//
//import lombok.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class JwtService
//{
//    @Value("${jwt.secret}")
//    private String jwtSecretKey;
//    @Value("${jwt.expiration}")
//    private Long jwtExpiration;
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//        claims.put("roles", roles);
//        return buildToken(claims, userDetails, jwtExpiration);
//    }
//
//
//}
