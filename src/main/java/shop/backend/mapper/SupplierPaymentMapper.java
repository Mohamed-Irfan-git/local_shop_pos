package shop.backend.mapper;

import org.springframework.stereotype.Component;
import shop.backend.dto.response.BankTransferDetailsResponse;
import shop.backend.dto.response.CardPaymentDetailsResponse;
import shop.backend.dto.response.ChequeDetailsResponse;
import shop.backend.dto.response.SupplierPaymentResponse;
import shop.backend.entity.BankTransferDetails;
import shop.backend.entity.CardPaymentDetails;
import shop.backend.entity.ChequeDetails;
import shop.backend.entity.SupplierPayment;

@Component
public class SupplierPaymentMapper {

    public ChequeDetailsResponse toResponse(ChequeDetails cd) {
        if (cd == null) return null;
        return ChequeDetailsResponse.builder()
                .id(cd.getId())
                .bankId(cd.getBank().getId())
                .bankName(cd.getBank().getName())
                .chequeNumber(cd.getChequeNumber())
                .chequeDate(cd.getChequeDate())
                .expectedPassDate(cd.getExpectedPassDate())
                .status(cd.getStatus())
                .build();
    }

    public CardPaymentDetailsResponse toResponse(CardPaymentDetails cd) {
        if (cd == null) return null;
        return CardPaymentDetailsResponse.builder()
                .id(cd.getId())
                .bankId(cd.getBank().getId())
                .bankName(cd.getBank().getName())
                .transactionReference(cd.getTransactionReference())
                .terminalReference(cd.getTerminalReference())
                .build();
    }

    public BankTransferDetailsResponse toResponse(BankTransferDetails bt) {
        if (bt == null) return null;
        return BankTransferDetailsResponse.builder()
                .id(bt.getId())
                .bankId(bt.getBank().getId())
                .bankName(bt.getBank().getName())
                .accountReference(bt.getAccountReference())
                .transactionReference(bt.getTransactionReference())
                .transferDate(bt.getTransferDate())
                .build();
    }

    public SupplierPaymentResponse toResponse(SupplierPayment payment) {
        if (payment == null) return null;
        return SupplierPaymentResponse.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .supplierId(payment.getSupplier().getId())
                .supplierName(payment.getSupplier().getName())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .reference(payment.getReference())
                .notes(payment.getNotes())
                .createdById(payment.getCreatedBy().getId())
                .createdByName(payment.getCreatedBy().getFullName())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .chequeDetails(toResponse(payment.getChequeDetails()))
                .cardDetails(toResponse(payment.getCardPaymentDetails()))
                .bankTransferDetails(toResponse(payment.getBankTransferDetails()))
                .build();
    }
}