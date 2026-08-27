package shop.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 50, unique = true)
    private String sku;

    @Column(name = "english_name", nullable = false, length = 150)
    private String englishName;

    @Column(name = "sinhala_name", length = 150)
    private String sinhalaName;

    @Column(name = "default_cost", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal defaultCost = BigDecimal.ZERO;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** Display name: Sinhala name when present, else English — matches the bill-printing rule in the spec. */
    @Transient
    public String getDisplayName() {
        return (sinhalaName != null && !sinhalaName.isBlank()) ? sinhalaName : englishName;
    }
}