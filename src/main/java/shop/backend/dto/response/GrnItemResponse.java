package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder @AllArgsConstructor
public class GrnItemResponse {
    private Long id;
    private Long productId;
    private String productDisplayName;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal total;
}