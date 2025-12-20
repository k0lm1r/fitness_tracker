package com.kolmir.fitness_tracker.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.utils.category.CategoryNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category getById(Long id) throws CategoryNotFoundException { 
        return categoryRepository.findById(id).orElseThrow(() -> 
                    new CategoryNotFoundException("Категория с таким id не найдена"));
    }
}
