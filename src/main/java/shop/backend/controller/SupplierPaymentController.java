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
import shop.backend.dto.request.ChequeStatusUpdateRequest;
import shop.backend.dto.request.SupplierPaymentRequest;
import shop.backend.dto.request.SupplierPaymentUpdateRequest;
import shop.backend.dto.response.ChequeDetailsResponse;
import shop.backend.dto.response.SupplierPaymentResponse;
import shop.backend.entity.enums.ChequeStatus;
import shop.backend.entity.enums.SupplierPaymentStatus;
import shop.backend.repository.CustomUserDetails;
import shop.backend.service.SupplierPaymentService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/supplier-payments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class SupplierPaymentController {

    private final SupplierPaymentService supplierPaymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> create(@Valid @RequestBody SupplierPaymentRequest request,
                                                                       @AuthenticationPrincipal CustomUserDetails principal) {
        SupplierPaymentResponse response = supplierPaymentService.create(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded", response));
    }

    /** Only amount/reference/notes/method-specific detail fields are editable — supplier is fixed (spec section 21). */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> update(@PathVariable Long id,
                                                                         @Valid @RequestBody SupplierPaymentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment updated", supplierPaymentService.update(id, request)));
    }

    /** Soft-cancel only — never physically deletes a payment (spec section 20). */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Payment cancelled", supplierPaymentService.cancel(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(supplierPaymentService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierPaymentResponse>>> search(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) SupplierPaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(supplierPaymentService.search(supplierId, status, start, end, page, size)));
    }

    /** Cheque Monitoring screen (spec section 22): search + status + bank filters over active cheque payments. */
    @GetMapping("/cheques")
    public ResponseEntity<ApiResponse<PageResponse<ChequeDetailsResponse>>> cheques(
            @RequestParam(required = false) ChequeStatus status,
            @RequestParam(required = false) Long bankId,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(supplierPaymentService.cheques(status, bankId, term, page, size)));
    }

    @PatchMapping("/{id}/cheque-status")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> updateChequeStatus(
            @PathVariable Long id, @Valid @RequestBody ChequeStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cheque status updated", supplierPaymentService.updateChequeStatus(id, request)));
    }
}