package shop.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.ChequeDetails;
import shop.backend.entity.enums.ChequeStatus;

import java.util.Optional;

public interface ChequeDetailsRepository extends JpaRepository<ChequeDetails, Long> {

    Optional<ChequeDetails> findBySupplierPaymentId(Long supplierPaymentId);

    /** Backs the Cheque Monitoring screen (spec section 22): search + status + bank filters, active payments only. */
    @Query("""
           select cd from ChequeDetails cd
           where cd.supplierPayment.status = shop.backend.entity.enums.SupplierPaymentStatus.ACTIVE
             and (:status is null or cd.status = :status)
             and (:bankId is null or cd.bank.id = :bankId)
             and (:term is null
                  or lower(cd.chequeNumber) like lower(concat('%', :term, '%'))
                  or lower(cd.supplierPayment.supplier.name) like lower(concat('%', :term, '%')))
           order by cd.chequeDate desc
           """)
    Page<ChequeDetails> search(@Param("status") ChequeStatus status,
                                @Param("bankId") Long bankId,
                                @Param("term") String term,
                                Pageable pageable);
}