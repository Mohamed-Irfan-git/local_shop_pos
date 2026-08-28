package shop.backend.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.ExpenseCategoryRequest;
import shop.backend.dto.response.ExpenseCategoryResponse;
import shop.backend.entity.ExpenseCategory;
import shop.backend.mapper.ExpenseMapper;
import shop.backend.repository.ExpenseCategoryRepository;
import shop.backend.service.ExpenseCategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseCategoryResponse create(ExpenseCategoryRequest request) {
        ExpenseCategory category = ExpenseCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();
        return expenseMapper.toResponse(expenseCategoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseCategoryResponse> getAll(boolean activeOnly) {
        List<ExpenseCategory> categories = activeOnly
                ? expenseCategoryRepository.findByIsActiveTrue()
                : expenseCategoryRepository.findAll();
        return categories.stream().map(expenseMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        ExpenseCategory category = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ExpenseCategory", id));
        category.setIsActive(false);
        expenseCategoryRepository.save(category);
    }
}