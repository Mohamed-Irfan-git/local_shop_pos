package shop.backend.service;


import shop.backend.dto.response.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Every report is computed on demand from SALES/SALE_ITEMS/SALE_PAYMENTS/GRNS/SUPPLIER_PAYMENTS/EXPENSES —
 * deliberately no daily_sales/monthly_sales-style precomputed tables (spec section 23).
 */
public interface ReportService {

    SalesSummaryReportResponse salesSummary(LocalDate start, LocalDate end);

    List<ProductSalesReportResponse> productSales(LocalDate start, LocalDate end, Long productId);

    List<CashierSalesReportResponse> cashierSales(LocalDate start, LocalDate end);

    List<PaymentMethodReportResponse> salesByPaymentMethod(LocalDate start, LocalDate end);

    List<SupplierReportResponse> supplierReport();

    List<ExpenseReportResponse> expensesByCategory(LocalDate start, LocalDate end);

    List<PaymentMethodReportResponse> expensesByPaymentMethod(LocalDate start, LocalDate end);
}