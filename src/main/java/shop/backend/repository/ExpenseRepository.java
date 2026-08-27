package shop.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.backend.entity.Expense;
import shop.backend.entity.enums.ExpenseStatus;

import java.time.LocalDate;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    boolean existsByExpenseNumber(String expenseNumber);
    Optional<Expense> findByExpenseNumber(String expenseNumber);

    @Query("""
           select e from Expense e
           where (:categoryId is null or e.category.id = :categoryId)
             and (:status is null or e.status = :status)
             and e.expenseDate between :start and :end
           order by e.expenseDate desc
           """)
    Page<Expense> search(@Param("categoryId") Long categoryId,
                          @Param("status") ExpenseStatus status,
                          @Param("start") LocalDate start,
                          @Param("end") LocalDate end,
                          Pageable pageable);
}