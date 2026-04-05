package com.kolmir.fitness_tracker.services;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.category.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.CategoryMapper;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ExerciseService exerciseService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAllByOwnerId(CurrentUserProvider.getCurrentUserId()).stream()
                    .map(categoryMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public CategoryDTO getById(Long id) { 
        return categoryMapper.toDTO(categoryRepository.findById(id).orElseThrow(() -> 
                    new NotFoundException("Категория с таким id не найдена")));
    }

    @Transactional
    public CategoryDTO save(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setOwner(userRepository.getReferenceById(CurrentUserProvider.getCurrentUserId()));
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        if (!categoryRepository.existsById(id))
            throw new NotFoundException("невозможно обновить несуществующую категорию");

        Category category = categoryMapper.toEntity(categoryDTO);
        category.setId(id);
        category.setOwner(userRepository.getReferenceById(CurrentUserProvider.getCurrentUserId()));

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) {
        if (!categoryRepository.existsById(id))
            throw new NotFoundException("невозможно удалить несуществующую категорию");
        exerciseService.invalidateCache();
        categoryRepository.deleteById(id);
    }
}
