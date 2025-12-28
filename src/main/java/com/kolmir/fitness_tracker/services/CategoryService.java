package com.kolmir.fitness_tracker.services;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.CategoryNotFoundException;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAllByOwnerId(CurrentUserProvider.getCurrentUserId());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public Category getById() throws CategoryNotFoundException { 
        return categoryRepository.findById(CurrentUserProvider.getCurrentUserId()).orElseThrow(() -> 
                    new CategoryNotFoundException("Категория с таким id не найдена"));
    }

    @Transactional
    public Category save(Category category) {
        User owner = new User();
        owner.setId(CurrentUserProvider.getCurrentUserId());
        category.setOwner(owner);
        return categoryRepository.save(category);
    }

    @Transactional
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public Category update(Long id, Category category) throws CategoryNotFoundException {
        category.setId(id);

        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException("невозможно обновить несуществующую категорию");
        
        return categoryRepository.save(category);
    }

    @Transactional
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(id))
            throw new CategoryNotFoundException("невозможно удалить несуществующую категорию");
        categoryRepository.deleteById(id);
    }
}
