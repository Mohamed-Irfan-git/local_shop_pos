package shop.backend.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.BusinessException;
import shop.backend.comman.PageResponse;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.*;
import shop.backend.dto.response.ChequeDetailsResponse;
import shop.backend.dto.response.SupplierPaymentResponse;
import shop.backend.entity.*;
import shop.backend.entity.enums.ChequeStatus;
import shop.backend.entity.enums.PaymentMethod;
import shop.backend.entity.enums.SupplierPaymentStatus;
import shop.backend.mapper.SupplierPaymentMapper;
import shop.backend.repository.*;
import shop.backend.service.SupplierPaymentService;
import shop.backend.util.DocumentNumberGenerator;

import java.time.LocalDateTime;

/**
 * Implements the payment lifecycle from spec sections 11, 20, 21, 22:
 *  - a payment is free-standing against the supplier's running balance, not tied to one GRN
 *  - the method-specific detail table (cheque/card/transfer) is chosen by paymentMethod
 *  - wrong entries are CANCELLED, never deleted, to preserve financial history
 *  - the supplier on a payment can never be changed once created
 *  - cheque status transitions (PENDING/PASS/RETURN) happen from the Cheque Monitoring screen
 */
@Service
@RequiredArgsConstructor
public class SupplierPaymentServiceImpl implements SupplierPaymentService {

    private final SupplierPaymentRepository supplierPaymentRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final ChequeDetailsRepository chequeDetailsRepository;
    private final SupplierPaymentMapper mapper;
    private final DocumentNumberGenerator numberGenerator;

    @Override
    @Transactional
    public SupplierPaymentResponse create(SupplierPaymentRequest request, Long userId) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> ResourceNotFoundException.of("Supplier", request.getSupplierId()));
        User createdBy = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        validateMethodDetailsPresent(request.getPaymentMethod(), request.getChequeDetails(),
                request.getCardDetails(), request.getBankTransferDetails());

        SupplierPayment payment = SupplierPayment.builder()
                .paymentNumber(numberGenerator.nextSupplierPaymentNumber())
                .supplier(supplier)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(SupplierPaymentStatus.ACTIVE)
                .paymentDate(LocalDateTime.now())
                .reference(request.getReference())
                .notes(request.getNotes())
                .createdBy(createdBy)
                .build();

        attachDetails(payment, request.getPaymentMethod(), request.getChequeDetails(),
                request.getCardDetails(), request.getBankTransferDetails());

        return mapper.toResponse(supplierPaymentRepository.save(payment));
    }

    @Override
    @Transactional
    public SupplierPaymentResponse update(Long id, SupplierPaymentUpdateRequest request) {
        SupplierPayment payment = findEntity(id);
        if (payment.getStatus() == SupplierPaymentStatus.CANCELLED) {
            throw new BusinessException("Cannot edit a cancelled payment — create a new one instead");
        }

        if (request.getAmount() != null) {
            payment.setAmount(request.getAmount());
        }
        if (request.getReference() != null) {
            payment.setReference(request.getReference());
        }
        if (request.getNotes() != null) {
            payment.setNotes(request.getNotes());
        }

        // Only the detail block matching the payment's original method is honoured —
        // payment method itself is not editable, same rule as supplier.
        if (payment.getPaymentMethod() == PaymentMethod.CHEQUE && request.getChequeDetails() != null) {
            ChequeDetailsRequest cd = request.getChequeDetails();
            ChequeDetails details = payment.getChequeDetails();
            details.setBank(findBank(cd.getBankId()));
            details.setChequeNumber(cd.getChequeNumber());
            details.setChequeDate(cd.getChequeDate());
            details.setExpectedPassDate(cd.getExpectedPassDate());
        } else if (payment.getPaymentMethod() == PaymentMethod.CARD && request.getCardDetails() != null) {
            CardPaymentDetailsRequest cd = request.getCardDetails();
            CardPaymentDetails details = payment.getCardPaymentDetails();
            details.setBank(findBank(cd.getBankId()));
            details.setTransactionReference(cd.getTransactionReference());
            details.setTerminalReference(cd.getTerminalReference());
        } else if (payment.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && request.getBankTransferDetails() != null) {
            BankTransferDetailsRequest bd = request.getBankTransferDetails();
            BankTransferDetails details = payment.getBankTransferDetails();
            details.setBank(findBank(bd.getBankId()));
            details.setAccountReference(bd.getAccountReference());
            details.setTransactionReference(bd.getTransactionReference());
            details.setTransferDate(bd.getTransferDate());
        }

        return mapper.toResponse(supplierPaymentRepository.save(payment));
    }

    @Override
    @Transactional
    public SupplierPaymentResponse cancel(Long id) {
        SupplierPayment payment = findEntity(id);
        if (payment.getStatus() == SupplierPaymentStatus.CANCELLED) {
            throw new BusinessException("Payment is already cancelled");
        }
        payment.setStatus(SupplierPaymentStatus.CANCELLED);
        return mapper.toResponse(supplierPaymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPaymentResponse getById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SupplierPaymentResponse> search(Long supplierId, SupplierPaymentStatus status,
                                                        LocalDateTime start, LocalDateTime end, int page, int size) {
        LocalDateTime rangeStart = start != null ? start : LocalDateTime.now().minusYears(5);
        LocalDateTime rangeEnd = end != null ? end : LocalDateTime.now();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        var result = supplierPaymentRepository.search(supplierId, status, rangeStart, rangeEnd, pageable)
                .map(mapper::toResponse);
        return PageResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ChequeDetailsResponse> cheques(ChequeStatus status, Long bankId, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "chequeDate"));
        var result = chequeDetailsRepository.search(status, bankId, term, pageable).map(mapper::toResponse);
        return PageResponse.from(result);
    }

    @Override
    @Transactional
    public SupplierPaymentResponse updateChequeStatus(Long paymentId, ChequeStatusUpdateRequest request) {
        SupplierPayment payment = findEntity(paymentId);
        if (payment.getPaymentMethod() != PaymentMethod.CHEQUE || payment.getChequeDetails() == null) {
            throw new BusinessException("Payment #" + payment.getPaymentNumber() + " is not a cheque payment");
        }
        if (payment.getStatus() == SupplierPaymentStatus.CANCELLED) {
            throw new BusinessException("Cannot update the cheque status of a cancelled payment");
        }
        payment.getChequeDetails().setStatus(request.getStatus());
        return mapper.toResponse(supplierPaymentRepository.save(payment));
    }

    // ---- helpers ----

    private void validateMethodDetailsPresent(PaymentMethod method, ChequeDetailsRequest cheque,
                                               CardPaymentDetailsRequest card, BankTransferDetailsRequest transfer) {
        switch (method) {
            case CHEQUE -> {
                if (cheque == null) throw new BusinessException("Cheque details are required for a CHEQUE payment");
            }
            case CARD -> {
                if (card == null) throw new BusinessException("Card details are required for a CARD payment");
            }
            case BANK_TRANSFER -> {
                if (transfer == null) throw new BusinessException("Bank transfer details are required for a BANK_TRANSFER payment");
            }
            case CASH -> { /* no detail table needed */ }
        }
    }

    private void attachDetails(SupplierPayment payment, PaymentMethod method, ChequeDetailsRequest cheque,
                                CardPaymentDetailsRequest card, BankTransferDetailsRequest transfer) {
        switch (method) {
            case CHEQUE -> payment.setChequeDetails(ChequeDetails.builder()
                    .supplierPayment(payment)
                    .bank(findBank(cheque.getBankId()))
                    .chequeNumber(cheque.getChequeNumber())
                    .chequeDate(cheque.getChequeDate())
                    .expectedPassDate(cheque.getExpectedPassDate())
                    .status(ChequeStatus.PENDING)
                    .build());
            case CARD -> payment.setCardPaymentDetails(CardPaymentDetails.builder()
                    .supplierPayment(payment)
                    .bank(findBank(card.getBankId()))
                    .transactionReference(card.getTransactionReference())
                    .terminalReference(card.getTerminalReference())
                    .build());
            case BANK_TRANSFER -> payment.setBankTransferDetails(BankTransferDetails.builder()
                    .supplierPayment(payment)
                    .bank(findBank(transfer.getBankId()))
                    .accountReference(transfer.getAccountReference())
                    .transactionReference(transfer.getTransactionReference())
                    .transferDate(transfer.getTransferDate())
                    .build());
            case CASH -> { /* nothing to attach */ }
        }
    }

    private Bank findBank(Long bankId) {
        return bankRepository.findById(bankId).orElseThrow(() -> ResourceNotFoundException.of("Bank", bankId));
    }

    private SupplierPayment findEntity(Long id) {
        return supplierPaymentRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("SupplierPayment", id));
    }
}