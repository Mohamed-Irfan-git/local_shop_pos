package shop.backend.service;


import shop.backend.comman.PageResponse;
import shop.backend.dto.request.SalePaymentRequest;
import shop.backend.dto.request.SaleRequest;
import shop.backend.dto.response.SaleResponse;
import shop.backend.entity.enums.SaleStatus;

import java.time.LocalDateTime;

public interface SaleService {
    SaleResponse create(SaleRequest request, Long cashierId);
    SaleResponse getById(Long id);
    SaleResponse getBySaleNumber(String saleNumber);
    PageResponse<SaleResponse> search(SaleStatus status, LocalDateTime start, LocalDateTime end,
                                      Long cashierId, int page, int size);
    SaleResponse addPayment(Long saleId, SalePaymentRequest request);
    SaleResponse cancel(Long saleId);
}