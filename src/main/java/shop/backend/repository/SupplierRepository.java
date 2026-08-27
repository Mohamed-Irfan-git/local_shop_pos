package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.Supplier;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsActiveTrue();
}