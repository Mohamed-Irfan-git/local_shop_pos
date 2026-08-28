package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Builder @AllArgsConstructor
public class SalesSummaryReportResponse {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private long totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal grossProfit;
    private BigDecimal totalDiscount;
}