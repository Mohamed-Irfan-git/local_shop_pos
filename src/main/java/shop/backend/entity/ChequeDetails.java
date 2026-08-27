package shop.backend.entity;


import jakarta.persistence.*;
import lombok.*;
import shop.backend.entity.enums.ChequeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cheque_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChequeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_payment_id", nullable = false, unique = true)
    private SupplierPayment supplierPayment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "cheque_number", nullable = false, length = 50)
    private String chequeNumber;

    @Column(name = "cheque_date", nullable = false)
    private LocalDate chequeDate;

    @Column(name = "expected_pass_date")
    private LocalDate expectedPassDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChequeStatus status = ChequeStatus.PENDING;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
    }
}