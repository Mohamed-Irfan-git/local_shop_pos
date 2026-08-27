package shop.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.Grn;
import shop.backend.entity.enums.GrnStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GrnRepository extends JpaRepository<Grn, Long> {

    boolean existsByGrnNumber(String grnNumber);
    Optional<Grn> findByGrnNumber(String grnNumber);

    List<Grn> findBySupplierIdAndStatus(Long supplierId, GrnStatus status);

    @Query("""
           select g from Grn g
           where (:supplierId is null or g.supplier.id = :supplierId)
             and (:status is null or g.status = :status)
             and g.receivedDate between :start and :end
           order by g.receivedDate desc
           """)
    Page<Grn> search(@Param("supplierId") Long supplierId,
                      @Param("status") GrnStatus status,
                      @Param("start") LocalDateTime start,
                      @Param("end") LocalDateTime end,
                      Pageable pageable);

    /** Sum of CONFIRMED GRNs for one supplier — half of the outstanding-balance formula (spec section 19). */
    @Query("""
           select coalesce(sum(g.total), 0) from Grn g
           where g.supplier.id = :supplierId and g.status = shop.backend.entity.enums.GrnStatus.CONFIRMED
           """)
    BigDecimal sumConfirmedTotalBySupplier(@Param("supplierId") Long supplierId);
}