package com.kolmir.fitness_tracker.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.utils.category.CategoryNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Category> getAll(Long ownerId) {
        return categoryRepository.findAllByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@categoryRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public Category getById(Long id) throws CategoryNotFoundException { 
        return categoryRepository.findById(id).orElseThrow(() -> 
                    new CategoryNotFoundException("Категория с таким id не найдена"));
    }

    @Transactional
    public Category save(Category category) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        category.setOwner(user);
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

    public Category DTOtoEntity(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        if (categoryDTO.getOwnerId() != null)
            category.setOwner(userRepository.getReferenceById(categoryDTO.getOwnerId()));
        return category;
    }

    public CategoryDTO entityToDTO(Category category) {
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        if (category.getOwner() != null)
            categoryDTO.setOwnerId(category.getOwner().getId());
        return categoryDTO;
    }
}
