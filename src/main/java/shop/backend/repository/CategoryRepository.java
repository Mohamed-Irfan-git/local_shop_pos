package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}