package shop.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.backend.entity.enums.ExpenseStatus;
import shop.backend.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private String expenseNumber;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private ExpenseStatus status;
    private LocalDate expenseDate;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}