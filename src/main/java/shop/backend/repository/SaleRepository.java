package shop.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.Sale;
import shop.backend.entity.enums.SaleStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    boolean existsBySaleNumber(String saleNumber);
    Optional<Sale> findBySaleNumber(String saleNumber);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
           select s from Sale s
           where s.status = :status
             and s.createdAt between :start and :end
             and (:cashierId is null or s.cashier.id = :cashierId)
           order by s.createdAt desc
           """)
    Page<Sale> search(@Param("status") SaleStatus status,
                       @Param("start") LocalDateTime start,
                       @Param("end") LocalDateTime end,
                       @Param("cashierId") Long cashierId,
                       Pageable pageable);
}