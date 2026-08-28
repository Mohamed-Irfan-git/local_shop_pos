package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.PaymentMethod;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class PaymentMethodReportResponse {
    private PaymentMethod paymentMethod;
    private long transactionCount;
    private BigDecimal totalAmount;
}