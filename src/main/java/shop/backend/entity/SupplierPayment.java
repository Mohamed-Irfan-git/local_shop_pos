package shop.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import shop.backend.entity.enums.PaymentMethod;
import shop.backend.entity.enums.SupplierPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_number", nullable = false, unique = true, length = 30)
    private String paymentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SupplierPaymentStatus status = SupplierPaymentStatus.ACTIVE;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 100)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "supplierPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChequeDetails chequeDetails;

    @OneToOne(mappedBy = "supplierPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    private CardPaymentDetails cardPaymentDetails;

    @OneToOne(mappedBy = "supplierPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankTransferDetails bankTransferDetails;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.paymentDate == null) this.paymentDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}