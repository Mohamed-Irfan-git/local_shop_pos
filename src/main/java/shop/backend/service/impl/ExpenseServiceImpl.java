package shop.backend.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.BusinessException;
import shop.backend.comman.PageResponse;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.ExpenseRequest;
import shop.backend.dto.response.ExpenseResponse;
import shop.backend.entity.Expense;
import shop.backend.entity.ExpenseCategory;
import shop.backend.entity.User;
import shop.backend.entity.enums.ExpenseStatus;
import shop.backend.mapper.ExpenseMapper;
import shop.backend.repository.ExpenseCategoryRepository;
import shop.backend.repository.ExpenseRepository;
import shop.backend.repository.UserRepository;
import shop.backend.service.ExpenseService;
import shop.backend.util.DocumentNumberGenerator;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;
    private final DocumentNumberGenerator numberGenerator;

    @Override
    @Transactional
    public ExpenseResponse create(ExpenseRequest request, Long userId) {
        ExpenseCategory category = findCategory(request.getCategoryId());
        User createdBy = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        Expense expense = Expense.builder()
                .expenseNumber(numberGenerator.nextExpenseNumber())
                .category(category)
                .description(request.getDescription())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(request.getStatus() != null ? request.getStatus() : ExpenseStatus.PAID)
                .expenseDate(request.getExpenseDate())
                .createdBy(createdBy)
                .build();

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = findEntity(id);
        if (expense.getStatus() == ExpenseStatus.CANCELLED) {
            throw new BusinessException("Cannot edit a cancelled expense");
        }

        expense.setCategory(findCategory(request.getCategoryId()));
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setExpenseDate(request.getExpenseDate());
        if (request.getStatus() != null) {
            expense.setStatus(request.getStatus());
        }

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public ExpenseResponse cancel(Long id) {
        Expense expense = findEntity(id);
        if (expense.getStatus() == ExpenseStatus.CANCELLED) {
            throw new BusinessException("Expense is already cancelled");
        }
        expense.setStatus(ExpenseStatus.CANCELLED);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getById(Long id) {
        return expenseMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExpenseResponse> search(Long categoryId, ExpenseStatus status, LocalDate start, LocalDate end,
                                                int page, int size) {
        LocalDate rangeStart = start != null ? start : LocalDate.now().minusYears(5);
        LocalDate rangeEnd = end != null ? end : LocalDate.now();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate"));
        var result = expenseRepository.search(categoryId, status, rangeStart, rangeEnd, pageable).map(expenseMapper::toResponse);
        return PageResponse.from(result);
    }

    private ExpenseCategory findCategory(Long id) {
        return expenseCategoryRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("ExpenseCategory", id));
    }

    private Expense findEntity(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Expense", id));
    }
}