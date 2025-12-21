package com.kolmir.fitness_tracker.controllersTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolmir.fitness_tracker.controllers.CategoryController;
import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.services.CategoryService;
import com.kolmir.fitness_tracker.utils.FitnessTrackerExceptionHandler;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new FitnessTrackerExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturnUserCategories() throws Exception {
        User user = new User();
        user.setId(5L);

        Category category = new Category();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Cardio");

        when(categoryService.getAll(5L)).thenReturn(List.of(category));
        when(categoryService.entityToDTO(category)).thenReturn(categoryDTO);

        var result = categoryController.getAll(user);

        assertEquals(1, result.size());
        assertEquals("Cardio", result.get(0).getName());
        verify(categoryService).getAll(5L);
    }

    @Test
    void getById_ShouldReturnCategory() throws Exception {
        Category category = new Category();
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Yoga");

        when(categoryService.getById(1L)).thenReturn(category);
        when(categoryService.entityToDTO(category)).thenReturn(dto);

        mockMvc.perform(get("/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga"));
    }

    @Test
    void create_ShouldReturnCreatedCategory() throws Exception {
        CategoryDTO request = new CategoryDTO();
        request.setName("Stretching");

        Category entity = new Category();
        CategoryDTO response = new CategoryDTO();
        response.setName("Stretching");

        when(categoryService.DTOtoEntity(any(CategoryDTO.class))).thenReturn(entity);
        when(categoryService.save(entity)).thenReturn(entity);
        when(categoryService.entityToDTO(entity)).thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Stretching"));
    }

    @Test
    void delete_ShouldCallService() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 2L))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(2L);
    }
}
