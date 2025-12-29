package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.CategoryNotFoundException;
import com.kolmir.fitness_tracker.mappers.CategoryMapper;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private User authenticatedUser;

    @BeforeEach
    void setUpAuth() {
        authenticatedUser = new User();
        authenticatedUser.setId(77L);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null));
    }

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAll_ReturnsUserCategories() {
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();

        when(categoryRepository.findAllByOwnerId(authenticatedUser.getId())).thenReturn(List.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(dto);

        List<CategoryDTO> result = categoryService.getAll();

        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
        verify(categoryRepository).findAllByOwnerId(authenticatedUser.getId());
    }

    @Test
    void getById_ReturnsCategory() throws CategoryNotFoundException {
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(dto);

        CategoryDTO result = categoryService.getById(5L);

        assertSame(dto, result);
        verify(categoryRepository).findById(5L);
    }

    @Test
    void getById_WhenMissing_Throws() {
        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getById(3L));
        verify(categoryRepository).findById(3L);
    }

    @Test
    void save_SetsOwnerFromSecurityContext() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Test");

        Category mappedEntity = new Category();
        CategoryDTO responseDto = new CategoryDTO();

        when(categoryMapper.toEntity(dto)).thenReturn(mappedEntity);
        when(categoryRepository.save(mappedEntity)).thenReturn(mappedEntity);
        when(categoryMapper.toDTO(mappedEntity)).thenReturn(responseDto);

        CategoryDTO result = categoryService.save(dto);

        assertSame(responseDto, result);
        assertEquals(authenticatedUser.getId(), dto.getOwnerId());
        verify(categoryRepository).save(mappedEntity);
    }

    @Test
    void update_SetsIdAndPersists() throws CategoryNotFoundException {
        CategoryDTO dto = new CategoryDTO();
        Category entity = new Category();
        CategoryDTO updatedDto = new CategoryDTO();

        when(categoryMapper.toEntity(dto)).thenReturn(entity);
        when(categoryRepository.existsById(9L)).thenReturn(true);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.toDTO(entity)).thenReturn(updatedDto);

        CategoryDTO result = categoryService.update(9L, dto);

        assertSame(updatedDto, result);
        assertEquals(9L, entity.getId());
        verify(categoryRepository).existsById(9L);
    }

    @Test
    void delete_WhenMissing_Throws() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(99L));
        verify(categoryRepository).existsById(99L);
    }

    @Test
    void delete_WhenExists_Deletes() throws CategoryNotFoundException {
        when(categoryRepository.existsById(10L)).thenReturn(true);

        categoryService.delete(10L);

        verify(categoryRepository).deleteById(10L);
    }
}
