package shop.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.backend.entity.enums.PaymentMethod;

import java.math.BigDecimal;

/**
 * Free-standing payment against a supplier's running balance (spec section 11) —
 * NOT tied to a specific GRN. Exactly one of cheque/card/bankTransfer details must be
 * present when paymentMethod requires it; validated in the service layer since the
 * rule spans multiple optional fields.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SupplierPaymentRequest {

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String reference;
    private String notes;

    @Valid
    private ChequeDetailsRequest chequeDetails;

    @Valid
    private CardPaymentDetailsRequest cardDetails;

    @Valid
    private BankTransferDetailsRequest bankTransferDetails;
}