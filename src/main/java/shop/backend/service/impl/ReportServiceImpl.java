package shop.backend.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.dto.response.*;
import shop.backend.entity.Expense;
import shop.backend.entity.SaleItem;
import shop.backend.entity.SalePayment;
import shop.backend.entity.enums.ExpenseStatus;
import shop.backend.entity.enums.PaymentMethod;
import shop.backend.repository.*;
import shop.backend.service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final SaleItemRepository saleItemRepository;
    private final SalePaymentRepository salePaymentRepository;
    private final SupplierRepository supplierRepository;
    private final GrnRepository grnRepository;
    private final SupplierPaymentRepository supplierPaymentRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public SalesSummaryReportResponse salesSummary(LocalDate start, LocalDate end) {
        List<SaleItem> lines = itemsInRange(start, end, null);

        BigDecimal revenue = sumOf(lines, li -> li.getUnitPrice().multiply(li.getQuantity()));
        BigDecimal cost = sumOf(lines, li -> li.getUnitCost().multiply(li.getQuantity()));
        BigDecimal discount = sumOf(lines, SaleItem::getDiscount);
        long saleCount = lines.stream().map(li -> li.getSale().getId()).distinct().count();

        return SalesSummaryReportResponse.builder()
                .periodStart(start)
                .periodEnd(end)
                .totalSales(saleCount)
                .totalRevenue(revenue)
                .totalCost(cost)
                .grossProfit(revenue.subtract(cost))
                .totalDiscount(discount)
                .build();
    }

    @Override
    public List<ProductSalesReportResponse> productSales(LocalDate start, LocalDate end, Long productId) {
        List<SaleItem> lines = itemsInRange(start, end, productId);

        Map<Long, List<SaleItem>> byProduct = lines.stream()
                .collect(Collectors.groupingBy(li -> li.getProduct().getId()));

        return byProduct.values().stream().map(group -> {
            var product = group.get(0).getProduct();
            BigDecimal qty = group.stream().map(SaleItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal revenue = sumOf(group, li -> li.getUnitPrice().multiply(li.getQuantity()));
            BigDecimal cost = sumOf(group, li -> li.getUnitCost().multiply(li.getQuantity()));
            return ProductSalesReportResponse.builder()
                    .productId(product.getId())
                    .productDisplayName(product.getDisplayName())
                    .categoryName(product.getCategory().getName())
                    .quantitySold(qty)
                    .revenue(revenue)
                    .cost(cost)
                    .grossProfit(revenue.subtract(cost))
                    .build();
        })
        .sorted(Comparator.comparing(ProductSalesReportResponse::getRevenue).reversed())
        .collect(Collectors.toList());
    }

    @Override
    public List<CashierSalesReportResponse> cashierSales(LocalDate start, LocalDate end) {
        List<SaleItem> lines = itemsInRange(start, end, null);

        Map<Long, List<SaleItem>> byCashier = lines.stream()
                .collect(Collectors.groupingBy(li -> li.getSale().getCashier().getId()));

        return byCashier.values().stream().map(group -> {
            var cashier = group.get(0).getSale().getCashier();
            long txCount = group.stream().map(li -> li.getSale().getId()).distinct().count();
            BigDecimal totalSales = sumOf(group, li -> li.getUnitPrice().multiply(li.getQuantity()).subtract(li.getDiscount()));

            List<Long> saleIds = group.stream().map(li -> li.getSale().getId()).distinct().collect(Collectors.toList());
            BigDecimal collected = salePaymentRepository.findForReport(atStartOfDay(start), atEndOfDay(end)).stream()
                    .filter(p -> saleIds.contains(p.getSale().getId()))
                    .map(SalePayment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return CashierSalesReportResponse.builder()
                    .cashierId(cashier.getId())
                    .cashierName(cashier.getFullName())
                    .transactionCount(txCount)
                    .totalSales(totalSales)
                    .totalCollected(collected)
                    .build();
        })
        .sorted(Comparator.comparing(CashierSalesReportResponse::getTotalSales).reversed())
        .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodReportResponse> salesByPaymentMethod(LocalDate start, LocalDate end) {
        List<SalePayment> payments = salePaymentRepository.findForReport(atStartOfDay(start), atEndOfDay(end));
        return groupByPaymentMethod(payments, SalePayment::getPaymentMethod, SalePayment::getAmount);
    }

    @Override
    public List<SupplierReportResponse> supplierReport() {
        return supplierRepository.findAll().stream().map(supplier -> {
            BigDecimal totalGrn = grnRepository.sumConfirmedTotalBySupplier(supplier.getId());
            BigDecimal totalPaid = supplierPaymentRepository.sumActiveTotalBySupplier(supplier.getId());
            long grnCount = grnRepository.findBySupplierIdAndStatus(supplier.getId(),
                    com.restaurant.pos.entity.enums.GrnStatus.CONFIRMED).size();

            return SupplierReportResponse.builder()
                    .supplierId(supplier.getId())
                    .supplierName(supplier.getName())
                    .totalGrns(grnCount)
                    .totalPurchased(totalGrn)
                    .totalPaid(totalPaid)
                    .outstanding(totalGrn.subtract(totalPaid))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ExpenseReportResponse> expensesByCategory(LocalDate start, LocalDate end) {
        List<Expense> expenses = expenseRepository.search(null, ExpenseStatus.PAID, start, end,
                        org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        Map<String, List<Expense>> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().getName()));

        return byCategory.entrySet().stream().map(entry -> ExpenseReportResponse.builder()
                        .groupLabel(entry.getKey())
                        .count(entry.getValue().size())
                        .totalAmount(sumOf(entry.getValue(), Expense::getAmount))
                        .build())
                .sorted(Comparator.comparing(ExpenseReportResponse::getTotalAmount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodReportResponse> expensesByPaymentMethod(LocalDate start, LocalDate end) {
        List<Expense> expenses = expenseRepository.search(null, ExpenseStatus.PAID, start, end,
                        org.springframework.data.domain.Pageable.unpaged())
                .getContent();
        return groupByPaymentMethod(expenses, Expense::getPaymentMethod, Expense::getAmount);
    }

    // ---- helpers ----

    private List<SaleItem> itemsInRange(LocalDate start, LocalDate end, Long productId) {
        return saleItemRepository.findForReport(atStartOfDay(start), atEndOfDay(end), productId);
    }

    private LocalDateTime atStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime atEndOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
    }

    private <T> BigDecimal sumOf(List<T> items, Function<T, BigDecimal> extractor) {
        return items.stream().map(extractor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private <T> List<PaymentMethodReportResponse> groupByPaymentMethod(
            List<T> items, Function<T, PaymentMethod> methodExtractor, Function<T, BigDecimal> amountExtractor) {

        Map<PaymentMethod, List<T>> grouped = items.stream().collect(Collectors.groupingBy(methodExtractor));

        return grouped.entrySet().stream().map(entry -> PaymentMethodReportResponse.builder()
                        .paymentMethod(entry.getKey())
                        .transactionCount(entry.getValue().size())
                        .totalAmount(sumOf(entry.getValue(), amountExtractor))
                        .build())
                .sorted(Comparator.comparing(PaymentMethodReportResponse::getTotalAmount).reversed())
                .collect(Collectors.toList());
    }
}