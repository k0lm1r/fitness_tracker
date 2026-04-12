package com.kolmir.fitness_tracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.controllers.api.RaceConditionApi;
import com.kolmir.fitness_tracker.dto.exercise.RaceConditionDemoResponse;
import com.kolmir.fitness_tracker.services.RaceConditionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/race-condition")
public class RaceConditionController implements RaceConditionApi {

    private final RaceConditionService raceConditionService;

    @GetMapping("/demo")
    @Override
    public ResponseEntity<RaceConditionDemoResponse> raceConditionDemo() {
        return ResponseEntity.ok(raceConditionService.runRaceConditionDemo());
    }
}
