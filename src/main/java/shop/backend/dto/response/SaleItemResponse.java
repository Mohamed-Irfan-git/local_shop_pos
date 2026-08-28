package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class SaleItemResponse {
    private Long id;
    private Long productId;
    private String productDisplayName;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal total;
}