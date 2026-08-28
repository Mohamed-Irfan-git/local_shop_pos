package shop.backend.service;


import shop.backend.dto.request.RoleRequest;
import shop.backend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    RoleResponse update(Long id, RoleRequest request);
    RoleResponse getById(Long id);
    List<RoleResponse> getAll();
    void delete(Long id);
}