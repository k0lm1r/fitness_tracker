package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.day.DayCreateRequest;
import com.kolmir.fitness_tracker.dto.day.DayResponse;
import com.kolmir.fitness_tracker.dto.day.DayUpdateRequest;
import com.kolmir.fitness_tracker.exceptions.DayAlreadyExistsException;
import com.kolmir.fitness_tracker.exceptions.DayNotFoundException;
import com.kolmir.fitness_tracker.services.DayService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/days")
public class DayController {
    private final DayService dayService;

    @GetMapping
    public List<DayResponse> getAll() {
        return dayService.getAllDays();
    }
    
    @GetMapping("/{date}")
    public ResponseEntity<DayResponse> getByDate(@PathVariable 
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") 
                                                LocalDate date) throws DayNotFoundException {
        return ResponseEntity.ok(dayService.getByDate(date));
    }

    @PostMapping
    public ResponseEntity<DayResponse> createDay(@RequestBody DayCreateRequest request) throws DayAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(dayService.create(request));
    }
    
    @PatchMapping("/{date}")
    public ResponseEntity<DayResponse> updateDay(@PathVariable 
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") 
                                                LocalDate date, 
                                                @RequestBody DayUpdateRequest request
                                            ) throws DayNotFoundException {
        return ResponseEntity.ok(dayService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable Long id) throws DayNotFoundException {
        dayService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
