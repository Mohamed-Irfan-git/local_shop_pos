package shop.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import shop.backend.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(length = 100)
    private String reference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}