package shop.backend.mapper;

import org.springframework.stereotype.Component;
import shop.backend.dto.response.GrnItemResponse;
import shop.backend.dto.response.GrnResponse;
import shop.backend.entity.Grn;
import shop.backend.entity.GrnItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GrnMapper {

    public GrnItemResponse toItemResponse(GrnItem item) {
        if (item == null) return null;
        return GrnItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productDisplayName(item.getProduct().getDisplayName())
                .quantity(item.getQuantity())
                .unitCost(item.getUnitCost())
                .total(item.getTotal())
                .build();
    }

    public GrnResponse toResponse(Grn grn) {
        if (grn == null) return null;

        List<GrnItemResponse> items = grn.getItems() == null ? Collections.emptyList()
                : grn.getItems().stream().map(this::toItemResponse).collect(Collectors.toList());

        return GrnResponse.builder()
                .id(grn.getId())
                .grnNumber(grn.getGrnNumber())
                .supplierId(grn.getSupplier().getId())
                .supplierName(grn.getSupplier().getName())
                .receivedDate(grn.getReceivedDate())
                .subtotal(grn.getSubtotal())
                .discount(grn.getDiscount())
                .total(grn.getTotal())
                .status(grn.getStatus())
                .createdById(grn.getCreatedBy().getId())
                .createdByName(grn.getCreatedBy().getFullName())
                .createdAt(grn.getCreatedAt())
                .updatedAt(grn.getUpdatedAt())
                .items(items)
                .build();
    }
}