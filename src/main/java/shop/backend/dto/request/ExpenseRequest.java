package shop.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.backend.entity.enums.ExpenseStatus;
import shop.backend.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private ExpenseStatus status;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;
}