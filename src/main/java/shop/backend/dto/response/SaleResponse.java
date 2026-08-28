package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.SaleStatus;
import shop.backend.entity.enums.SaleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder @AllArgsConstructor
public class SaleResponse {
    private Long id;
    private String saleNumber;
    private Long cashierId;
    private String cashierName;
    private SaleType saleType;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private BigDecimal amountPaid;
    private BigDecimal balanceDue;
    private SaleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SaleItemResponse> items;
    private List<SalePaymentResponse> payments;
}