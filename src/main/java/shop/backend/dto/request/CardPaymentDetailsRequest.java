package shop.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CardPaymentDetailsRequest {

    @NotNull(message = "Bank is required for a card payment")
    private Long bankId;

    private String transactionReference;
    private String terminalReference;
}