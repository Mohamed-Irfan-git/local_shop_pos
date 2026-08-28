package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class SupplierReportResponse {
    private Long supplierId;
    private String supplierName;
    private long totalGrns;
    private BigDecimal totalPurchased;
    private BigDecimal totalPaid;
    private BigDecimal outstanding;
}