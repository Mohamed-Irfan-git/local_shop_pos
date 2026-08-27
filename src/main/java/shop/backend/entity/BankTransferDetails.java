package shop.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "bank_transfer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransferDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_payment_id", nullable = false, unique = true)
    private SupplierPayment supplierPayment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "account_reference", length = 100)
    private String accountReference;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "transfer_date")
    private LocalDate transferDate;
}