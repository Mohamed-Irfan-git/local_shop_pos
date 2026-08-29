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
import shop.backend.dto.request.SalePaymentRequest;
import shop.backend.dto.request.SaleRequest;
import shop.backend.dto.response.SaleResponse;
import shop.backend.entity.enums.SaleStatus;
import shop.backend.repository.CustomUserDetails;
import shop.backend.service.SaleService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
    public ResponseEntity<ApiResponse<SaleResponse>> create(@Valid @RequestBody SaleRequest request,
                                                            @AuthenticationPrincipal CustomUserDetails principal) {
        SaleResponse response = saleService.create(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Sale completed", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(saleService.getById(id)));
    }

    @GetMapping("/by-number/{saleNumber}")
    public ResponseEntity<ApiResponse<SaleResponse>> getBySaleNumber(@PathVariable String saleNumber) {
        return ResponseEntity.ok(ApiResponse.success(saleService.getBySaleNumber(saleNumber)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SaleResponse>>> search(
            @RequestParam(required = false) SaleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) Long cashierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(saleService.search(status, start, end, cashierId, page, size)));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
    public ResponseEntity<ApiResponse<SaleResponse>> addPayment(@PathVariable Long id,
                                                                  @Valid @RequestBody SalePaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment added", saleService.addPayment(id, request)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<SaleResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Sale cancelled", saleService.cancel(id)));
    }
}