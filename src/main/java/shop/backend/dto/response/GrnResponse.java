package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.GrnStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder @AllArgsConstructor
public class GrnResponse {
    private Long id;
    private String grnNumber;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime receivedDate;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private GrnStatus status;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GrnItemResponse> items;
}