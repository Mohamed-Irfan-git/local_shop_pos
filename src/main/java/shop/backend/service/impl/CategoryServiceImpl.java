package shop.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.backend.comman.DuplicateResourceException;
import shop.backend.comman.ResourceNotFoundException;
import shop.backend.dto.request.CategoryRequest;
import shop.backend.dto.response.CategoryResponse;
import shop.backend.entity.Category;
import shop.backend.mapper.CategoryMapper;
import shop.backend.repository.CategoryRepository;
import shop.backend.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category '" + request.getName() + "' already exists");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntity(id);
        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category '" + request.getName() + "' already exists");
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return categoryMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(boolean activeOnly) {
        List<Category> categories = activeOnly ? categoryRepository.findByIsActiveTrue() : categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deactivate(Long id) {
        Category category = findEntity(id);
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    private Category findEntity(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Category", id));
    }
}