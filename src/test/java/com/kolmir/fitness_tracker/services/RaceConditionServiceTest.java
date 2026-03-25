package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.kolmir.fitness_tracker.dto.exercise.RaceConditionDemoResponse;

class RaceConditionServiceTest {

    private final RaceConditionService raceConditionService = new RaceConditionService();

    @Test
    void raceConditionDemoShowsUnsafeCounterCanLoseUpdates() {
        RaceConditionDemoResponse result = raceConditionService.runRaceConditionDemo(64, 20_000);

        assertEquals(result.getExpected(), result.getAtomicCounter());
        assertTrue(result.getUnsafeCounter() <= result.getExpected());
        assertEquals(result.getExpected() - result.getUnsafeCounter(), result.getLostUpdates());
    }
}
