package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/** Dynamically computed: total CONFIRMED GRNs minus total ACTIVE payments (spec section 19) — never stored. */
@Getter
@Builder
@AllArgsConstructor
public class SupplierBalanceResponse {
    private Long supplierId;
    private String supplierName;
    private BigDecimal totalGrn;
    private BigDecimal totalPaid;
    private BigDecimal outstanding;
}