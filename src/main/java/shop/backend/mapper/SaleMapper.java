package shop.backend.mapper;

import org.springframework.stereotype.Component;
import shop.backend.dto.response.SaleItemResponse;
import shop.backend.dto.response.SalePaymentResponse;
import shop.backend.dto.response.SaleResponse;
import shop.backend.entity.Sale;
import shop.backend.entity.SaleItem;
import shop.backend.entity.SalePayment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaleMapper {

    public SaleItemResponse toItemResponse(SaleItem item) {
        if (item == null) return null;
        return SaleItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productDisplayName(item.getProduct().getDisplayName())
                .quantity(item.getQuantity())
                .unitCost(item.getUnitCost())
                .unitPrice(item.getUnitPrice())
                .discount(item.getDiscount())
                .total(item.getTotal())
                .build();
    }

    public SalePaymentResponse toPaymentResponse(SalePayment payment) {
        if (payment == null) return null;
        return SalePaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .reference(payment.getReference())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public SaleResponse toResponse(Sale sale) {
        if (sale == null) return null;

        List<SaleItemResponse> items = sale.getItems() == null ? Collections.emptyList()
                : sale.getItems().stream().map(this::toItemResponse).collect(Collectors.toList());

        List<SalePaymentResponse> payments = sale.getPayments() == null ? Collections.emptyList()
                : sale.getPayments().stream().map(this::toPaymentResponse).collect(Collectors.toList());

        BigDecimal amountPaid = payments.stream()
                .map(SalePaymentResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balanceDue = sale.getTotal().subtract(amountPaid);

        return SaleResponse.builder()
                .id(sale.getId())
                .saleNumber(sale.getSaleNumber())
                .cashierId(sale.getCashier().getId())
                .cashierName(sale.getCashier().getFullName())
                .saleType(sale.getSaleType())
                .subtotal(sale.getSubtotal())
                .discount(sale.getDiscount())
                .total(sale.getTotal())
                .amountPaid(amountPaid)
                .balanceDue(balanceDue)
                .status(sale.getStatus())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .items(items)
                .payments(payments)
                .build();
    }
}