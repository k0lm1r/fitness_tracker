package com.kolmir.fitness_tracker.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.CategoryNotFoundException;
import com.kolmir.fitness_tracker.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) throws CategoryNotFoundException {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody @Valid CategoryDTO categoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.save(categoryDTO)); 
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody CategoryDTO categoryDTO) throws CategoryNotFoundException {
        
        CategoryDTO updatedcategory = categoryService.update(id, categoryDTO);
        return ResponseEntity.ok(updatedcategory);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws CategoryNotFoundException {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
