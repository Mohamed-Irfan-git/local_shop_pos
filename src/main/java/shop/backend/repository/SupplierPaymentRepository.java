package shop.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.SupplierPayment;
import shop.backend.entity.enums.SupplierPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, Long> {

    boolean existsByPaymentNumber(String paymentNumber);
    Optional<SupplierPayment> findByPaymentNumber(String paymentNumber);

    @Query("""
           select sp from SupplierPayment sp
           where (:supplierId is null or sp.supplier.id = :supplierId)
             and (:status is null or sp.status = :status)
             and sp.paymentDate between :start and :end
           order by sp.paymentDate desc
           """)
    Page<SupplierPayment> search(@Param("supplierId") Long supplierId,
                                  @Param("status") SupplierPaymentStatus status,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  Pageable pageable);

    /** Sum of ACTIVE payments for one supplier — other half of the outstanding-balance formula (spec section 19). */
    @Query("""
           select coalesce(sum(sp.amount), 0) from SupplierPayment sp
           where sp.supplier.id = :supplierId and sp.status = shop.backend.entity.enums.SupplierPaymentStatus.ACTIVE
           """)
    BigDecimal sumActiveTotalBySupplier(@Param("supplierId") Long supplierId);
}