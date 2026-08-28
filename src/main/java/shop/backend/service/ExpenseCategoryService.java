package shop.backend.service;

import shop.backend.dto.request.ExpenseCategoryRequest;
import shop.backend.dto.response.ExpenseCategoryResponse;

import java.util.List;

public interface ExpenseCategoryService {
    ExpenseCategoryResponse create(ExpenseCategoryRequest request);
    List<ExpenseCategoryResponse> getAll(boolean activeOnly);
    void deactivate(Long id);
}