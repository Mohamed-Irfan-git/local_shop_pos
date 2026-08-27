package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    Optional<Product> findBySkuIgnoreCase(String sku);

    /** Backs product search box: matches English name, Sinhala name or SKU (see spec section 4). */
    @Query("""
           select p from Product p
           where p.isActive = true
             and (lower(p.englishName) like lower(concat('%', :term, '%'))
                  or lower(p.sinhalaName) like lower(concat('%', :term, '%'))
                  or lower(p.sku) like lower(concat('%', :term, '%')))
           """)
    List<Product> search(@Param("term") String term);
}