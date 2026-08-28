package shop.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.backend.comman.ApiResponse;
import shop.backend.comman.PageResponse;
import shop.backend.dto.request.ExpenseRequest;
import shop.backend.dto.response.ExpenseResponse;
import shop.backend.entity.enums.ExpenseStatus;
import shop.backend.repository.CustomUserDetails;
import shop.backend.service.ExpenseService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails principal) {
        ExpenseResponse response = expenseService.create(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Expense recorded", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Expense updated", expenseService.update(id, request)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ExpenseResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Expense cancelled", expenseService.cancel(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ExpenseResponse>>> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.search(categoryId, status, start, end, page, size)));
    }
}


//AuthController.java
//BankController.java
//CategoryController.java
//ExpenseCategoryController.java
//ExpenseController.java
//GrnController.java
//ProductController.java
//ReportController.java
//RoleController.java
//SaleController.java
//SupplierController.java
//SupplierPaymentController.java
//UserController.java
