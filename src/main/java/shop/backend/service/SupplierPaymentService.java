package shop.backend.service;



import shop.backend.comman.PageResponse;
import shop.backend.dto.request.ChequeStatusUpdateRequest;
import shop.backend.dto.request.SupplierPaymentRequest;
import shop.backend.dto.request.SupplierPaymentUpdateRequest;
import shop.backend.dto.response.ChequeDetailsResponse;
import shop.backend.dto.response.SupplierPaymentResponse;
import shop.backend.entity.enums.ChequeStatus;
import shop.backend.entity.enums.SupplierPaymentStatus;

import java.time.LocalDateTime;

public interface SupplierPaymentService {

    SupplierPaymentResponse create(SupplierPaymentRequest request, Long userId);

    /** Editable fields only — supplier can never change (spec section 21). */
    SupplierPaymentResponse update(Long id, SupplierPaymentUpdateRequest request);

    /** Soft-cancel: status -> CANCELLED, record is kept for audit history (spec section 20). */
    SupplierPaymentResponse cancel(Long id);

    SupplierPaymentResponse getById(Long id);

    PageResponse<SupplierPaymentResponse> search(Long supplierId, SupplierPaymentStatus status,
                                                 LocalDateTime start, LocalDateTime end, int page, int size);

    /** Cheque Monitoring screen: search + status + bank filters over active cheque payments (spec section 22). */
    PageResponse<ChequeDetailsResponse> cheques(ChequeStatus status, Long bankId, String term, int page, int size);

    SupplierPaymentResponse updateChequeStatus(Long paymentId, ChequeStatusUpdateRequest request);
}