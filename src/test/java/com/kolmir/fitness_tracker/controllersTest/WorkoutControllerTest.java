package com.kolmir.fitness_tracker.controllersTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolmir.fitness_tracker.controllers.WorkoutsController;
import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.services.WorkoutService;

@WebMvcTest(WorkoutsController.class)
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutService workoutService;

    @Test
    void index_ShouldReturnWorkoutList() throws Exception {
        Workout firstWorkout = new Workout();
        Workout secondWorkout = new Workout();

        WorkoutDTO firstDto = new WorkoutDTO();
        firstDto.setName("Morning Run");
        firstDto.setCalories(300);

        WorkoutDTO secondDto = new WorkoutDTO();
        secondDto.setName("Evening Yoga");
        secondDto.setCalories(120);

        when(workoutService.getAllByOwnerId()).thenReturn(List.of(firstWorkout, secondWorkout));
        when(workoutService.entityToDTO(firstWorkout)).thenReturn(firstDto);
        when(workoutService.entityToDTO(secondWorkout)).thenReturn(secondDto);

        mockMvc.perform(get("/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Morning Run"))
                .andExpect(jsonPath("$[1].calories").value(120));
    }

    @Test
    void getById_ShouldReturnWorkoutDTO() throws Exception {
        Workout workout = new Workout();
        workout.setId(1L);

        WorkoutDTO dto = new WorkoutDTO();
        dto.setCalories(15);

        when(workoutService.getById(1L)).thenReturn(workout);
        when(workoutService.entityToDTO(workout)).thenReturn(dto);

        mockMvc.perform(get("/workouts/{id}", 1L))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedWorkout"))
                .andExpect(jsonPath("$.calories").value(250));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/workouts/{id}", 7L))
                .andExpect(status().isNoContent());
    }

    private WorkoutDTO buildRequestDto() {
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setCalories(200);
        workoutDTO.setCategoryId(1L);
        workoutDTO.setDurationMinutes(45);
        workoutDTO.setName("testWorkout");
        workoutDTO.setOwnerId(1L);
        workoutDTO.setWorkoutDate(LocalDateTime.now().plusDays(1));
        return workoutDTO;
    }
}
