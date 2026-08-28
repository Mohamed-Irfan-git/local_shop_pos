package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class SalePaymentResponse {
    private Long id;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String reference;
    private LocalDateTime createdAt;
}


