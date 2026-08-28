package shop.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shop.backend.dto.request.LoginRequest;
import shop.backend.dto.response.LoginResponse;
import shop.backend.repository.CustomUserDetails;
import shop.backend.security.JwtUtil;
import shop.backend.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(principal.getUsername(), principal.getUserId(), principal.getAuthorities());

        String role = principal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(principal.getUserId())
                .username(principal.getUsername())
                .fullName(principal.getFullName())
                .role(role)
                .build();
    }
}