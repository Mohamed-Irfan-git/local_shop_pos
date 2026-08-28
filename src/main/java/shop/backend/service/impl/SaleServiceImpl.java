package shop.backend.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.BusinessException;
import shop.backend.comman.PageResponse;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.SaleItemRequest;
import shop.backend.dto.request.SalePaymentRequest;
import shop.backend.dto.request.SaleRequest;
import shop.backend.dto.response.SaleResponse;
import shop.backend.entity.*;
import shop.backend.entity.enums.SaleStatus;
import shop.backend.mapper.SaleMapper;
import shop.backend.repository.ProductRepository;
import shop.backend.repository.SaleRepository;
import shop.backend.repository.UserRepository;
import shop.backend.service.SaleService;
import shop.backend.util.DocumentNumberGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sale creation is one atomic DB transaction: item price/cost snapshots, totals,
 * and any up-front payments all commit together (or none do). This matters because
 * multiple POS terminals write to the same database concurrently (see architectural
 * note in the schema doc).
 */
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleMapper saleMapper;
    private final DocumentNumberGenerator numberGenerator;

    @Override
    @Transactional
    public SaleResponse create(SaleRequest request, Long cashierId) {
        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", cashierId));

        Sale sale = Sale.builder()
                .saleNumber(numberGenerator.nextSaleNumber())
                .cashier(cashier)
                .saleType(request.getSaleType())
                .status(SaleStatus.COMPLETED)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        List<SaleItem> items = new ArrayList<>();

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Product", itemReq.getProductId()));

            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new BusinessException("Product '" + product.getDisplayName() + "' is not active");
            }

            BigDecimal quantity = itemReq.getQuantity();
            BigDecimal lineDiscount = itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO;

            // Cost/price are ALWAYS taken live from the catalog here and then frozen onto the line —
            // never trusted from the client, and never re-read later (spec section 6).
            BigDecimal unitCost = product.getDefaultCost();
            BigDecimal unitPrice = product.getSellingPrice();
            BigDecimal lineTotal = unitPrice.multiply(quantity).subtract(lineDiscount);

            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(quantity)
                    .unitCost(unitCost)
                    .unitPrice(unitPrice)
                    .discount(lineDiscount)
                    .total(lineTotal)
                    .build();

            items.add(item);
            subtotal = subtotal.add(unitPrice.multiply(quantity));
        }

        BigDecimal billDiscount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal linesDiscountTotal = items.stream().map(SaleItem::getDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.subtract(linesDiscountTotal).subtract(billDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Total discount cannot exceed the sale subtotal");
        }

        sale.setSubtotal(subtotal);
        sale.setDiscount(linesDiscountTotal.add(billDiscount));
        sale.setTotal(total);
        sale.setItems(items);

        List<SalePayment> payments = new ArrayList<>();
        if (request.getPayments() != null) {
            for (SalePaymentRequest payReq : request.getPayments()) {
                payments.add(SalePayment.builder()
                        .sale(sale)
                        .amount(payReq.getAmount())
                        .paymentMethod(payReq.getPaymentMethod())
                        .reference(payReq.getReference())
                        .build());
            }
        }
        sale.setPayments(payments);

        Sale saved = saleRepository.save(sale);
        return saleMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getById(Long id)  {
        return saleMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getBySaleNumber(String saleNumber) {
        Sale sale = saleRepository.findBySaleNumber(saleNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleNumber));
        return saleMapper.toResponse(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SaleResponse> search(SaleStatus status, LocalDateTime start, LocalDateTime end,
                                             Long cashierId, int page, int size) {
        LocalDateTime rangeStart = start != null ? start : LocalDateTime.now().minusYears(5);
        LocalDateTime rangeEnd = end != null ? end : LocalDateTime.now();
        SaleStatus effectiveStatus = status != null ? status : SaleStatus.COMPLETED;

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = saleRepository.search(effectiveStatus, rangeStart, rangeEnd, cashierId, pageable)
                .map(saleMapper::toResponse);
        return PageResponse.from(result);
    }

    @Override
    @Transactional
    public SaleResponse addPayment(Long saleId, SalePaymentRequest request) {
        Sale sale = findEntity(saleId);
        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("Cannot add a payment to a cancelled sale");
        }

        SalePayment payment = SalePayment.builder()
                .sale(sale)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .reference(request.getReference())
                .build();

        sale.getPayments().add(payment);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse cancel(Long saleId) {
        Sale sale = findEntity(saleId);
        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("Sale is already cancelled");
        }
        sale.setStatus(SaleStatus.CANCELLED);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    private Sale findEntity(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Sale", id));
    }
}