package com.kolmir.fitness_tracker.controllersTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolmir.fitness_tracker.controllers.WorkoutsController;
import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.security.JwtAuthenticationFilter;
import com.kolmir.fitness_tracker.services.WorkoutService;
import com.kolmir.fitness_tracker.utils.FitnessTrackerExceptionHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WorkoutControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private WorkoutService workoutService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private WorkoutsController workoutsController;

    @BeforeEach
    void setup() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(workoutsController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new FitnessTrackerExceptionHandler())
                .build();
    }

    @Test
    void getById_ShouldReturnWorkoutDTO() throws Exception {
        Workout workout = new Workout();
        workout.setId(1L);

        WorkoutDTO dto = new WorkoutDTO();
        dto.setCalories(15);

        when(workoutService.getById(1L)).thenReturn(workout);
        when(workoutService.entityToDTO(workout)).thenReturn(dto);

        mockMvc.perform(get("/workouts/{id}", 1L)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calories").value(15));
    }

    @Test
    void create_ShouldPersistWorkoutAndReturnDTO() throws Exception {
        WorkoutDTO requestDto = buildRequestDto();
        Workout entity = new Workout();
        entity.setCalories(200);

        WorkoutDTO responseDto = new WorkoutDTO();
        responseDto.setCalories(200);
        responseDto.setName("testWorkout");

        when(workoutService.DTOtoEntity(any(WorkoutDTO.class))).thenReturn(entity);
        when(workoutService.save(entity)).thenReturn(entity);
        when(workoutService.entityToDTO(entity)).thenReturn(responseDto);

        mockMvc.perform(post("/workouts")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("testWorkout"))
                .andExpect(jsonPath("$.calories").value(200));
    }

    @Test
    void update_ShouldReturnUpdatedDTO() throws Exception {
        WorkoutDTO requestDto = buildRequestDto();
        Workout entity = new Workout();
        entity.setCalories(250);

        WorkoutDTO responseDto = new WorkoutDTO();
        responseDto.setCalories(250);
        responseDto.setName("updatedWorkout");

        when(workoutService.DTOtoEntity(any(WorkoutDTO.class))).thenReturn(entity);
        when(workoutService.save(entity)).thenReturn(entity);
        when(workoutService.entityToDTO(entity)).thenReturn(responseDto);

        mockMvc.perform(put("/workouts/{id}", 5L)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedWorkout"))
                .andExpect(jsonPath("$.calories").value(250));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/workouts/{id}", 7L)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isNoContent());

        verify(workoutService).delete(anyLong());
    }

    @Test
    void getAllWithFilters_ShouldReturnPagedWorkouts() throws Exception {
        User user = new User();
        user.setId(1L);

        Workout workout1 = new Workout();
        Workout workout2 = new Workout();

        WorkoutDTO dto1 = new WorkoutDTO();
        dto1.setName("Morning Run");
        WorkoutDTO dto2 = new WorkoutDTO();
        dto2.setName("Evening Yoga");

        Page<Workout> page = new PageImpl<>(List.of(workout1, workout2), PageRequest.of(0, 20), 2);

        when(workoutService.getAllByOwnerId(any(WorkoutFilter.class), any(Pageable.class)))
                .thenReturn(page);
        when(workoutService.entityToDTO(workout1)).thenReturn(dto1);
        when(workoutService.entityToDTO(workout2)).thenReturn(dto2);

        Page<WorkoutDTO> result = workoutsController.getAllWithFilters(user, new WorkoutFilter(), PageRequest.of(0, 20));

        assertEquals(2, result.getTotalElements());
        assertEquals("Morning Run", result.getContent().get(0).getName());
        assertEquals("Evening Yoga", result.getContent().get(1).getName());

        ArgumentCaptor<WorkoutFilter> filterCaptor = ArgumentCaptor.forClass(WorkoutFilter.class);
        verify(workoutService).getAllByOwnerId(filterCaptor.capture(), any(Pageable.class));
        assertEquals(1L, filterCaptor.getValue().getOwnerId());
    }

    private WorkoutDTO buildRequestDto() {
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setCalories(200);
        workoutDTO.setCategoryId(1L);
        workoutDTO.setDurationMinutes(45);
        workoutDTO.setName("testWorkout");
        workoutDTO.setWorkoutDate(LocalDateTime.now());
        return workoutDTO;
    }

    private UsernamePasswordAuthenticationToken authenticatedUser() {
        User user = new User();
        user.setId(1L);
        return new UsernamePasswordAuthenticationToken(user, null);
    }
}
