package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.fitness_tracker.dto.category.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.CategoryMapper;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllReturnsMappedCategoriesForCurrentUser() {
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(7L);
            when(categoryRepository.findAllByOwnerId(7L)).thenReturn(List.of(category));
            when(categoryMapper.toDTO(category)).thenReturn(dto);

            List<CategoryDTO> result = categoryService.getAll();

            assertEquals(1, result.size());
            assertEquals(dto, result.getFirst());
        }
    }

    @Test
    void getByIdThrowsWhenCategoryMissing() {
        when(categoryRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getById(11L));
    }

    @Test
    void getByIdReturnsMappedCategoryWhenFound() {
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();

        when(categoryRepository.findById(12L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(dto);

        CategoryDTO result = categoryService.getById(12L);

        assertEquals(dto, result);
    }

    @Test
    void saveAssignsOwnerAndPersistsCategory() {
        CategoryDTO request = new CategoryDTO();
        request.setName("Cardio");

        Category category = new Category();
        CategoryDTO response = new CategoryDTO();
        var owner = new com.kolmir.fitness_tracker.models.User();
        owner.setId(5L);

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(5L);
            when(categoryMapper.toEntity(request)).thenReturn(category);
            when(userRepository.getReferenceById(5L)).thenReturn(owner);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toDTO(category)).thenReturn(response);

            CategoryDTO result = categoryService.save(request);

            assertEquals(response, result);
            verify(categoryRepository).save(category);
        }
    }

    @Test
    void updateThrowsWhenCategoryMissing() {
        CategoryDTO request = new CategoryDTO();
        when(categoryRepository.existsById(100L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.update(100L, request));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateSavesMappedCategoryWhenExists() {
        CategoryDTO request = new CategoryDTO();
        Category category = new Category();
        CategoryDTO response = new CategoryDTO();
        var owner = new com.kolmir.fitness_tracker.models.User();
        owner.setId(13L);

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(13L);
            when(categoryMapper.toEntity(request)).thenReturn(category);
            when(categoryRepository.existsById(13L)).thenReturn(true);
            when(userRepository.getReferenceById(13L)).thenReturn(owner);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toDTO(category)).thenReturn(response);

            CategoryDTO result = categoryService.update(13L, request);

            assertEquals(13L, category.getId());
            assertEquals(response, result);
        }
    }

    @Test
    void deleteThrowsWhenCategoryMissing() {
        when(categoryRepository.existsById(88L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.delete(88L));
        verify(categoryRepository, never()).deleteById(88L);
    }

    @Test
    void deleteInvalidatesExerciseCacheAndRemovesCategory() {
        when(categoryRepository.existsById(9L)).thenReturn(true);

        categoryService.delete(9L);

        verify(exerciseService).invalidateCache();
        verify(categoryRepository).deleteById(9L);
    }
}
