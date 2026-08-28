package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class ProductSalesReportResponse {
    private Long productId;
    private String productDisplayName;
    private String categoryName;
    private BigDecimal quantitySold;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal grossProfit;
}