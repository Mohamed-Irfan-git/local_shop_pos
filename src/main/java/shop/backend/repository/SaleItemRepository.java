package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.SaleItem;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleId(Long saleId);

    /** For product-wise sales reports: quantity/revenue/cost per product in a date range. */
    @Query("""
           select si from SaleItem si
           where si.sale.status = shop.backend.entity.enums.SaleStatus.COMPLETED
             and si.sale.createdAt between :start and :end
             and (:productId is null or si.product.id = :productId)
           """)
    List<SaleItem> findForReport(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("productId") Long productId);
}