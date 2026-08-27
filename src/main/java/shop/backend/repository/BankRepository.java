package shop.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.backend.entity.Bank;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findByIsActiveTrue();
}