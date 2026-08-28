package shop.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.backend.entity.enums.SaleType;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SaleRequest {

    @NotNull(message = "Sale type is required")
    private SaleType saleType;

    /** Overall bill-level discount, on top of any per-line discounts. Defaults to zero. */
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @NotEmpty(message = "Sale must contain at least one item")
    @Valid
    private List<SaleItemRequest> items;

    /** Payments can be provided at creation (typical POS flow: pay-and-print) or added afterwards. */
    @Valid
    private List<SalePaymentRequest> payments;
}