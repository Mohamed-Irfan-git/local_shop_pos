package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder @AllArgsConstructor
public class ExpenseCategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}