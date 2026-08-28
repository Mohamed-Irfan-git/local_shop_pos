package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class ExpenseReportResponse {
    private String groupLabel;   // category name, payment method, or date bucket depending on grouping
    private long count;
    private BigDecimal totalAmount;
}