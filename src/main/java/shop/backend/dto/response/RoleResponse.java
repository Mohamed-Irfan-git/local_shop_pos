package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}