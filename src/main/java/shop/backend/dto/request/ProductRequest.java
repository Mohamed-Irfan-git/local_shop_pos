package shop.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @Size(max = 50)
    private String sku;

    @NotBlank(message = "English name is required")
    @Size(max = 150)
    private String englishName;

    @Size(max = 150)
    private String sinhalaName;

    @NotNull(message = "Default cost is required")
    @DecimalMin(value = "0.0", message = "Cost cannot be negative")
    private BigDecimal defaultCost;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", message = "Selling price cannot be negative")
    private BigDecimal sellingPrice;

    private Boolean isActive;
}