package shop.backend.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.backend.comman.ApiResponse;
import shop.backend.dto.response.*;
import shop.backend.service.ReportService;

import java.time.LocalDate;
import java.util.List;

/**
 * All endpoints compute their result on demand from the transactional tables — see
 * ReportService — so there is nothing here to keep in sync (spec section 23).
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales/summary")
    public ResponseEntity<ApiResponse<SalesSummaryReportResponse>> salesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(reportService.salesSummary(start, end)));
    }

    @GetMapping("/sales/by-product")
    public ResponseEntity<ApiResponse<List<ProductSalesReportResponse>>> productSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long productId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.productSales(start, end, productId)));
    }

    @GetMapping("/sales/by-cashier")
    public ResponseEntity<ApiResponse<List<CashierSalesReportResponse>>> cashierSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(reportService.cashierSales(start, end)));
    }

    @GetMapping("/sales/by-payment-method")
    public ResponseEntity<ApiResponse<List<PaymentMethodReportResponse>>> salesByPaymentMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(reportService.salesByPaymentMethod(start, end)));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<ApiResponse<List<SupplierReportResponse>>> supplierReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.supplierReport()));
    }

    @GetMapping("/expenses/by-category")
    public ResponseEntity<ApiResponse<List<ExpenseReportResponse>>> expensesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(reportService.expensesByCategory(start, end)));
    }

    @GetMapping("/expenses/by-payment-method")
    public ResponseEntity<ApiResponse<List<PaymentMethodReportResponse>>> expensesByPaymentMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(reportService.expensesByPaymentMethod(start, end)));
    }
}