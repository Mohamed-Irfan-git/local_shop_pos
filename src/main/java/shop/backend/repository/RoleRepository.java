package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}