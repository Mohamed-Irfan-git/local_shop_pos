package shop.backend.service;


import shop.backend.dto.request.ProductRequest;
import shop.backend.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    ProductResponse getById(Long id);
    List<ProductResponse> getByCategory(Long categoryId);
    List<ProductResponse> search(String term);
    void deactivate(Long id);
}