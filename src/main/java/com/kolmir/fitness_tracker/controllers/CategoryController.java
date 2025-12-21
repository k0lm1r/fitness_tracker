package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.services.CategoryService;
import com.kolmir.fitness_tracker.utils.ErrorResponse;
import com.kolmir.fitness_tracker.utils.category.CategoryNotFoundException;
import com.kolmir.fitness_tracker.utils.category.CategoryNotValidException;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> getAll(@AuthenticationPrincipal User user) {
        return categoryService.getAll(user.getId()).stream().map(categoryService::entityToDTO).toList();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestParam Long id) throws CategoryNotFoundException {
        return ResponseEntity.ok(categoryService.entityToDTO(categoryService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CategoryDTO categoryDTO, BindingResult bindingResult) throws CategoryNotValidException {
        if (bindingResult.hasErrors())
            throw new CategoryNotValidException(ErrorResponse.getExceptionMessage(bindingResult));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.entityToDTO(categoryService.save(categoryService.DTOtoEntity(categoryDTO)))); 
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody CategoryDTO categoryDTO,
                    BindingResult bindingResult) throws CategoryNotValidException {
        
        if (bindingResult.hasErrors()) 
            throw new CategoryNotValidException(ErrorResponse.getExceptionMessage(bindingResult));
        
        CategoryDTO updatedcategory = categoryService.entityToDTO(
                categoryService.save(categoryService.DTOtoEntity(categoryDTO)));
        return ResponseEntity.ok(updatedcategory);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws CategoryNotFoundException {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
