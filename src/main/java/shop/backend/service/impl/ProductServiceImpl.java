package shop.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.DuplicateResourceException;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.ProductRequest;
import shop.backend.dto.response.ProductResponse;
import shop.backend.entity.Category;
import shop.backend.entity.Product;
import shop.backend.mapper.ProductMapper;
import shop.backend.repository.CategoryRepository;
import shop.backend.repository.ProductRepository;
import shop.backend.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        validateSkuUnique(request.getSku(), null);
        Category category = findCategory(request.getCategoryId());

        Product product = Product.builder()
                .category(category)
                .sku(request.getSku())
                .englishName(request.getEnglishName())
                .sinhalaName(request.getSinhalaName())
                .defaultCost(request.getDefaultCost())
                .sellingPrice(request.getSellingPrice())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntity(id);
        validateSkuUnique(request.getSku(), id);

        product.setCategory(findCategory(request.getCategoryId()));
        product.setSku(request.getSku());
        product.setEnglishName(request.getEnglishName());
        product.setSinhalaName(request.getSinhalaName());
        // Changing these does NOT touch historical sale_items — they store their own snapshot (spec section 6)
        product.setDefaultCost(request.getDefaultCost());
        product.setSellingPrice(request.getSellingPrice());
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String term) {
        return productRepository.search(term).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        Product product = findEntity(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    private void validateSkuUnique(String sku, Long currentId) {
        if (sku == null || sku.isBlank()) return;
        productRepository.findBySkuIgnoreCase(sku).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new DuplicateResourceException("SKU '" + sku + "' is already in use");
            }
        });
    }

    private Product findEntity(Long id) {
        return productRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Product", id));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", categoryId));
    }
}