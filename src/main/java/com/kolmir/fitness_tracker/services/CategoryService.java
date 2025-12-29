package com.kolmir.fitness_tracker.services;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.CategoryNotFoundException;
import com.kolmir.fitness_tracker.mappers.CategoryMapper;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAllByOwnerId(CurrentUserProvider.getCurrentUserId()).stream()
                    .map(categoryMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public CategoryDTO getById(Long id) throws CategoryNotFoundException { 
        return categoryMapper.toDTO(categoryRepository.findById(id).orElseThrow(() -> 
                    new CategoryNotFoundException("Категория с таким id не найдена")));
    }

    @Transactional
    public CategoryDTO save(CategoryDTO categoryDTO) {
        categoryDTO.setOwnerId(CurrentUserProvider.getCurrentUserId());
        Category category = categoryMapper.toEntity(categoryDTO);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) throws CategoryNotFoundException {
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setId(id);

        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException("невозможно обновить несуществующую категорию");
        
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException("невозможно удалить несуществующую категорию");
        categoryRepository.deleteById(id);
    }
}
