package shop.backend.service;


import shop.backend.dto.request.LoginRequest;
import shop.backend.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}