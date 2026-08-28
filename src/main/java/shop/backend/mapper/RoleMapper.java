package shop.backend.mapper;

import org.springframework.stereotype.Component;
import shop.backend.dto.response.RoleResponse;
import shop.backend.entity.Role;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        if (role == null) return null;
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .build();
    }
}