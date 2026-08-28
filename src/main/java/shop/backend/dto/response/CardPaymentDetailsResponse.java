package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder @AllArgsConstructor
public class CardPaymentDetailsResponse {
    private Long id;
    private Long bankId;
    private String bankName;
    private String transactionReference;
    private String terminalReference;
}