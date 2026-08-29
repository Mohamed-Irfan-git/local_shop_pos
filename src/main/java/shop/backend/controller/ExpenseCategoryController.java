package shop.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.backend.comman.ApiResponse;
import shop.backend.dto.request.ExpenseCategoryRequest;
import shop.backend.dto.response.ExpenseCategoryResponse;
import shop.backend.service.ExpenseCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ExpenseCategoryResponse>> create(@Valid @RequestBody ExpenseCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Expense category created", expenseCategoryService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseCategoryResponse>>> getAll(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(ApiResponse.success(expenseCategoryService.getAll(activeOnly)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        expenseCategoryService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Expense category deactivated", null));
    }
}