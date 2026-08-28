package shop.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Editable fields on an existing payment (spec section 21) — supplier is intentionally
 * excluded and can never be changed; to fix a wrong supplier, cancel + recreate.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SupplierPaymentUpdateRequest {

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String reference;
    private String notes;

    @Valid
    private ChequeDetailsRequest chequeDetails;

    @Valid
    private CardPaymentDetailsRequest cardDetails;

    @Valid
    private BankTransferDetailsRequest bankTransferDetails;
}