package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.BankTransferDetails;

import java.util.Optional;

public interface BankTransferDetailsRepository extends JpaRepository<BankTransferDetails, Long> {
    Optional<BankTransferDetails> findBySupplierPaymentId(Long supplierPaymentId);
}