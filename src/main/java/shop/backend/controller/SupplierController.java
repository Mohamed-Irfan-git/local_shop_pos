package shop.backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.backend.comman.ApiResponse;
import shop.backend.dto.request.SupplierRequest;
import shop.backend.dto.response.SupplierBalanceResponse;
import shop.backend.dto.response.SupplierResponse;
import shop.backend.service.SupplierService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Supplier created", supplierService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<SupplierResponse>> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Supplier updated", supplierService.update(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAll(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getAll(activeOnly)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        supplierService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deactivated", null));
    }

    /** Outstanding = confirmed GRNs - active payments, computed live, never stored (spec section 19). */
    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<SupplierBalanceResponse>> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getBalance(id)));
    }

    @GetMapping("/balances")
    public ResponseEntity<ApiResponse<List<SupplierBalanceResponse>>> getAllBalances() {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getAllBalances()));
    }
}