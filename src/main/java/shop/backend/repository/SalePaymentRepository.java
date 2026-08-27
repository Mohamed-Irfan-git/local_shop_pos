package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.SalePayment;

import java.time.LocalDateTime;
import java.util.List;

public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {

    List<SalePayment> findBySaleId(Long saleId);

    @Query("""
           select sp from SalePayment sp
           where sp.sale.status = shop.backend.entity.enums.SaleStatus.COMPLETED
             and sp.createdAt between :start and :end
           """)
    List<SalePayment> findForReport(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}