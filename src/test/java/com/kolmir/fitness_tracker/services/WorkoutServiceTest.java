package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.mappers.WorkoutMapper;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutMapper workoutMapper;

    @InjectMocks
    private WorkoutService workoutService;

    private User authenticatedUser;

    @BeforeEach
    void setUpAuth() {
        authenticatedUser = new User();
        authenticatedUser.setId(42L);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null));
    }

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllByOwnerId_ReturnsMappedPage() {
        WorkoutFilter filter = new WorkoutFilter();
        Pageable pageable = PageRequest.of(0, 10);

        Workout workout1 = new Workout();
        Workout workout2 = new Workout();
        Page<Workout> page = new PageImpl<>(List.of(workout1, workout2), pageable, 2);
        WorkoutDTO dto1 = new WorkoutDTO();
        WorkoutDTO dto2 = new WorkoutDTO();
        
        when(workoutRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(workoutMapper.toDTO(workout1)).thenReturn(dto1);
        when(workoutMapper.toDTO(workout2)).thenReturn(dto2);

        Page<WorkoutDTO> result = workoutService.getAllByOwnerId(filter, pageable);

        assertEquals(2, result.getTotalElements());
        assertSame(dto1, result.getContent().get(0));
        assertSame(dto2, result.getContent().get(1));
        verify(workoutRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById_WhenWorkoutExists_ReturnsDto() throws WorkoutNotFoundException {
        Workout workout = new Workout();
        WorkoutDTO dto = new WorkoutDTO();

        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));
        when(workoutMapper.toDTO(workout)).thenReturn(dto);

        WorkoutDTO result = workoutService.getById(1L);

        assertSame(dto, result);
        verify(workoutRepository).findById(1L);
    }

    @Test
    void getById_WhenWorkoutMissing_ThrowsException() {
        when(workoutRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(WorkoutNotFoundException.class, () -> workoutService.getById(5L));
        verify(workoutRepository).findById(5L);
    }

    @Test
    void save_ShouldSetOwnerAndReturnDto() {
        WorkoutDTO dto = new WorkoutDTO();
        dto.setName("Morning Run");

        Workout entity = new Workout();
        WorkoutDTO responseDto = new WorkoutDTO();

        when(workoutMapper.toEntity(dto)).thenReturn(entity);
        when(workoutRepository.save(entity)).thenReturn(entity);
        when(workoutMapper.toDTO(entity)).thenReturn(responseDto);

        WorkoutDTO result = workoutService.save(dto);

        assertSame(responseDto, result);
        assertEquals(authenticatedUser.getId(), dto.getOwnerId());
        verify(workoutRepository).save(entity);
    }

    @Test
    void update_ShouldSetIdAndReturnDto() throws WorkoutNotFoundException {
        WorkoutDTO dto = new WorkoutDTO();
        Workout entity = new Workout();
        WorkoutDTO responseDto = new WorkoutDTO();

        when(workoutMapper.toEntity(dto)).thenReturn(entity);
        when(workoutRepository.existsById(5L)).thenReturn(true);
        when(workoutRepository.save(entity)).thenReturn(entity);
        when(workoutMapper.toDTO(entity)).thenReturn(responseDto);

        WorkoutDTO result = workoutService.update(5L, dto);

        assertSame(responseDto, result);
        assertEquals(5L, entity.getId());
        verify(workoutRepository).existsById(5L);
    }

    @Test
    void delete_WhenWorkoutMissing_ThrowsException() {
        when(workoutRepository.existsById(9L)).thenReturn(false);

        assertThrows(WorkoutNotFoundException.class, () -> workoutService.delete(9L));
        verify(workoutRepository).existsById(9L);
    }

    @Test
    void delete_WhenWorkoutExists_DeletesIt() throws WorkoutNotFoundException {
        when(workoutRepository.existsById(3L)).thenReturn(true);

        workoutService.delete(3L);

        verify(workoutRepository).deleteById(3L);
    }
}
