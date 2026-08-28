package shop.backend.mapper;

import org.springframework.stereotype.Component;
import shop.backend.dto.response.ProductResponse;
import shop.backend.entity.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .sku(product.getSku())
                .englishName(product.getEnglishName())
                .sinhalaName(product.getSinhalaName())
                .displayName(product.getDisplayName())
                .defaultCost(product.getDefaultCost())
                .sellingPrice(product.getSellingPrice())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
