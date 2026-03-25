package com.kolmir.fitness_tracker.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.controllers.api.ExerciseApi;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskCreateResponse;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskStatusResponse;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;
import com.kolmir.fitness_tracker.services.ExerciseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/exercises")
public class ExerciseController implements ExerciseApi {
    private final ExerciseService exerciseService;

    @GetMapping
    @Override
    public Page<ExerciseResponse> getAllWithFilters(ExerciseFilter exerciseFilter, Pageable pageable) {
        exerciseFilter.setOwnerId(CurrentUserProvider.getCurrentUserId());
        return exerciseService.getAllByOwnerId(exerciseFilter, pageable);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) {
        return new ResponseEntity<>(exerciseService.getById(id), HttpStatus.OK);
    }
    
    @PostMapping
    @Override
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest exerciseDTO) {
        return new ResponseEntity<>(exerciseService.save(exerciseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ExerciseResponse> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody ExerciseRequest exerciseDTO) {
        return ResponseEntity.ok(exerciseService.update(id, exerciseDTO));
    }
    
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    } 
    
    @PostMapping("/bulk")
    @Override
    public ResponseEntity<List<ExerciseResponse>> bulkPost(@RequestBody List<@Valid ExerciseRequest> requests, 
                                                            @RequestParam Boolean withTransactional) {
        return ResponseEntity.ok(withTransactional ? 
            exerciseService.saveAllWithTransactional(requests) : 
            exerciseService.saveAllWithoutTransactional(requests)
        );
    }

    @PostMapping("/async/bulk")
    @Override
    public ResponseEntity<AsyncTaskCreateResponse> startAsyncBulkPost(@RequestBody List<@Valid ExerciseRequest> requests) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(exerciseService.startBulkSaveTask(requests));
    }

    @GetMapping("/async/tasks/{taskId}")
    @Override
    public ResponseEntity<AsyncTaskStatusResponse> getAsyncTaskStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(exerciseService.getTaskStatus(taskId));
    }
    
}
