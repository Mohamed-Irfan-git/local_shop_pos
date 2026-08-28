package shop.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.backend.entity.enums.ChequeStatus;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChequeStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ChequeStatus status;
}