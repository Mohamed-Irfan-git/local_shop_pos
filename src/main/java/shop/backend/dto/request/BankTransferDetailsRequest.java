package shop.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BankTransferDetailsRequest {

    @NotNull(message = "Bank is required for a bank transfer")
    private Long bankId;

    private String accountReference;
    private String transactionReference;
    private LocalDate transferDate;
}