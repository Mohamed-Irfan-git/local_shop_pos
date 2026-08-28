package shop.backend.service;


import shop.backend.comman.PageResponse;
import shop.backend.dto.request.GrnRequest;
import shop.backend.dto.response.GrnResponse;
import shop.backend.entity.enums.GrnStatus;

import java.time.LocalDateTime;

public interface GrnService {
    GrnResponse createDraft(GrnRequest request, Long userId);
    GrnResponse confirm(Long id);
    GrnResponse cancel(Long id);
    GrnResponse getById(Long id);
    PageResponse<GrnResponse> search(Long supplierId, GrnStatus status, LocalDateTime start, LocalDateTime end,
                                     int page, int size);
}