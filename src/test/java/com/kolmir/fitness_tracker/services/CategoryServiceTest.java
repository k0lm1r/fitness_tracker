package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.utils.category.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

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
        when(categoryRepository.findAllByOwnerId(1L)).thenReturn(List.of(category));

        List<Category> result = categoryService.getAll(1L);

        assertEquals(1, result.size());
        assertSame(category, result.get(0));
        verify(categoryRepository).findAllByOwnerId(1L);
    }

    @Test
    void getById_ReturnsCategory() throws CategoryNotFoundException {
        Category category = new Category();
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        Category result = categoryService.getById(5L);

        assertSame(category, result);
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
        Category category = new Category();
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.save(category);

        assertSame(authenticatedUser, result.getOwner());
        verify(categoryRepository).save(category);
    }

    @Test
    void update_SetsIdAndPersists() throws CategoryNotFoundException {
        Category category = new Category();

        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.update(9L, category);

        assertEquals(9L, result.getId());
        verify(categoryRepository).save(category);
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

    @Test
    void DTOtoEntity_MapsAndSetsOwner() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Test");
        dto.setOwnerId(88L);

        Category mappedCategory = new Category();
        mappedCategory.setName("Test");

        User owner = new User();
        owner.setId(88L);

        when(modelMapper.map(dto, Category.class)).thenReturn(mappedCategory);
        when(userRepository.getReferenceById(88L)).thenReturn(owner);

        Category result = categoryService.DTOtoEntity(dto);

        assertSame(owner, result.getOwner());
        assertEquals("Test", result.getName());
    }

    @Test
    void entityToDTO_MapsAndAddsOwnerId() {
        Category category = new Category();
        category.setName("Cardio");
        User owner = new User();
        owner.setId(33L);
        category.setOwner(owner);

        CategoryDTO mappedDto = new CategoryDTO();
        mappedDto.setName("Cardio");

        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(mappedDto);

        CategoryDTO result = categoryService.entityToDTO(category);

        assertEquals(33L, result.getOwnerId());
        assertEquals("Cardio", result.getName());
    }
}
