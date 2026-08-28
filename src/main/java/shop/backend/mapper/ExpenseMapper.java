package shop.backend.mapper;


import org.springframework.stereotype.Component;
import shop.backend.dto.response.ExpenseCategoryResponse;
import shop.backend.dto.response.ExpenseResponse;
import shop.backend.entity.Expense;
import shop.backend.entity.ExpenseCategory;

@Component
public class ExpenseMapper {

    public ExpenseCategoryResponse toResponse(ExpenseCategory category) {
        if (category == null) return null;
        return ExpenseCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .build();
    }

    public ExpenseResponse toResponse(Expense expense) {
        if (expense == null) return null;
        return ExpenseResponse.builder()
                .id(expense.getId())
                .expenseNumber(expense.getExpenseNumber())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .paymentMethod(expense.getPaymentMethod())
                .status(expense.getStatus())
                .expenseDate(expense.getExpenseDate())
                .createdById(expense.getCreatedBy().getId())
                .createdByName(expense.getCreatedBy().getFullName())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}