package shop.backend.service;


import shop.backend.dto.request.CategoryRequest;
import shop.backend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll(boolean activeOnly);
    void deactivate(Long id);
}