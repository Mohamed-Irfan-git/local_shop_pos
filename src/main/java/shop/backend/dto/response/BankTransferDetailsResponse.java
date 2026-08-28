package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter @Builder @AllArgsConstructor
public class BankTransferDetailsResponse {
    private Long id;
    private Long bankId;
    private String bankName;
    private String accountReference;
    private String transactionReference;
    private LocalDate transferDate;
}