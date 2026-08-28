package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.PaymentMethod;
import shop.backend.entity.enums.SupplierPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class SupplierPaymentResponse {
    private Long id;
    private String paymentNumber;
    private Long supplierId;
    private String supplierName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private SupplierPaymentStatus status;
    private LocalDateTime paymentDate;
    private String reference;
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ChequeDetailsResponse chequeDetails;
    private CardPaymentDetailsResponse cardDetails;
    private BankTransferDetailsResponse bankTransferDetails;
}