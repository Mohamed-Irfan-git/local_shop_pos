package shop.backend.service.impl;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.BusinessException;
import shop.backend.comman.PageResponse;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.GrnItemRequest;
import shop.backend.dto.request.GrnRequest;
import shop.backend.dto.response.GrnResponse;
import shop.backend.entity.*;
import shop.backend.entity.enums.GrnStatus;
import shop.backend.mapper.GrnMapper;
import shop.backend.repository.GrnRepository;
import shop.backend.repository.ProductRepository;
import shop.backend.repository.SupplierRepository;
import shop.backend.repository.UserRepository;
import shop.backend.service.GrnService;
import shop.backend.util.DocumentNumberGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GRNs are created as DRAFT and only affect a supplier's outstanding balance once CONFIRMED
 * (spec sections 9, 19) — this lets a user fix mistakes before they touch financial totals.
 * Confirm/cancel are separate atomic transactions so two terminals can't confirm/cancel
 * the same GRN into an inconsistent state.
 */
@Service
@RequiredArgsConstructor
public class GrnServiceImpl implements GrnService {

    private final GrnRepository grnRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final GrnMapper grnMapper;
    private final DocumentNumberGenerator numberGenerator;

    @Override
    @Transactional
    public GrnResponse createDraft(GrnRequest request, Long userId) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> ResourceNotFoundException.of("Supplier", request.getSupplierId()));
        User createdBy = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        Grn grn = Grn.builder()
                .grnNumber(numberGenerator.nextGrnNumber())
                .supplier(supplier)
                .receivedDate(request.getReceivedDate() != null ? request.getReceivedDate() : LocalDateTime.now())
                .status(GrnStatus.DRAFT)
                .createdBy(createdBy)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        List<GrnItem> items = new ArrayList<>();

        for (GrnItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Product", itemReq.getProductId()));

            BigDecimal lineTotal = itemReq.getUnitCost().multiply(itemReq.getQuantity());
            items.add(GrnItem.builder()
                    .grn(grn)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitCost(itemReq.getUnitCost())
                    .total(lineTotal)
                    .build());

            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Discount cannot exceed the GRN subtotal");
        }

        grn.setSubtotal(subtotal);
        grn.setDiscount(discount);
        grn.setTotal(total);
        grn.setItems(items);

        return grnMapper.toResponse(grnRepository.save(grn));
    }

    @Override
    @Transactional
    public GrnResponse confirm(Long id) {
        Grn grn = findEntity(id);
        if (grn.getStatus() != GrnStatus.DRAFT) {
            throw new BusinessException("Only a DRAFT GRN can be confirmed (current status: " + grn.getStatus() + ")");
        }
        grn.setStatus(GrnStatus.CONFIRMED);
        return grnMapper.toResponse(grnRepository.save(grn));
    }

    @Override
    @Transactional
    public GrnResponse cancel(Long id) {
        Grn grn = findEntity(id);
        if (grn.getStatus() == GrnStatus.CANCELLED) {
            throw new BusinessException("GRN is already cancelled");
        }
        // Cancelling a CONFIRMED GRN removes it from the supplier's outstanding total immediately,
        // since that total is always computed live from CONFIRMED-status GRNs (spec section 19).
        grn.setStatus(GrnStatus.CANCELLED);
        return grnMapper.toResponse(grnRepository.save(grn));
    }

    @Override
    @Transactional(readOnly = true)
    public GrnResponse getById(Long id) {
        return grnMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GrnResponse> search(Long supplierId, GrnStatus status, LocalDateTime start, LocalDateTime end,
                                            int page, int size) {
        LocalDateTime rangeStart = start != null ? start : LocalDateTime.now().minusYears(5);
        LocalDateTime rangeEnd = end != null ? end : LocalDateTime.now();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedDate"));
        var result = grnRepository.search(supplierId, status, rangeStart, rangeEnd, pageable).map(grnMapper::toResponse);
        return PageResponse.from(result);
    }

    private Grn findEntity(Long id) {
        return grnRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Grn", id));
    }
}