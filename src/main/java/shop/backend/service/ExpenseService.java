package shop.backend.service;


import shop.backend.comman.PageResponse;
import shop.backend.dto.request.ExpenseRequest;
import shop.backend.dto.response.ExpenseResponse;
import shop.backend.entity.enums.ExpenseStatus;

import java.time.LocalDate;

public interface ExpenseService {
    ExpenseResponse create(ExpenseRequest request, Long userId);
    ExpenseResponse update(Long id, ExpenseRequest request);
    ExpenseResponse cancel(Long id);
    ExpenseResponse getById(Long id);
    PageResponse<ExpenseResponse> search(Long categoryId, ExpenseStatus status, LocalDate start, LocalDate end,
                                         int page, int size);
}