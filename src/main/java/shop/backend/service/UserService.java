package shop.backend.service;


import shop.backend.dto.request.UserRequest;
import shop.backend.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    UserResponse getById(Long id);
    List<UserResponse> getAll(Boolean isActive);
    void deactivate(Long id);
    void activate(Long id);
}