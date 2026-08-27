package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.CardPaymentDetails;

import java.util.Optional;

public interface CardPaymentDetailsRepository extends JpaRepository<CardPaymentDetails, Long> {
    Optional<CardPaymentDetails> findBySupplierPaymentId(Long supplierPaymentId);
}