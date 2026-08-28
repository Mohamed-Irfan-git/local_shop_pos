package shop.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String username;

    /** Only required on create; on update, null/blank means "keep the existing password". */
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 150)
    private String fullName;

    @NotNull(message = "Role is required")
    private Long roleId;

    private Boolean isActive;
}