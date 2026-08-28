package shop.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChequeDetailsRequest {

    @NotNull(message = "Bank is required for a cheque payment")
    private Long bankId;

    @NotBlank(message = "Cheque number is required")
    private String chequeNumber;

    @NotNull(message = "Cheque date is required")
    private LocalDate chequeDate;

    private LocalDate expectedPassDate;
}