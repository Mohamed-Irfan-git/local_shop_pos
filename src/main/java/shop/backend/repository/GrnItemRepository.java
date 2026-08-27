package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.GrnItem;

import java.util.List;

public interface GrnItemRepository extends JpaRepository<GrnItem, Long> {
    List<GrnItem> findByGrnId(Long grnId);
}