package shop.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BankRequest {

    @NotBlank(message = "Bank name is required")
    @Size(max = 100)
    private String name;

    private Boolean isActive;
}