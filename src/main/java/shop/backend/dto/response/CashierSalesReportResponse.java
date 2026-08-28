package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class CashierSalesReportResponse {
    private Long cashierId;
    private String cashierName;
    private long transactionCount;
    private BigDecimal totalSales;
    private BigDecimal totalCollected;
}