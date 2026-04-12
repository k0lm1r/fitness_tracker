package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void defaultRaceConditionDemoUsesPreconfiguredValues() {
        RaceConditionDemoResponse result = raceConditionService.runRaceConditionDemo();

        assertEquals(64, result.getThreadCount());
        assertEquals(250_000, result.getIncrementsPerThread());
    }

    @Test
    void raceConditionDemoThrowsWhenCurrentThreadInterrupted() {
        Thread.currentThread().interrupt();
        try {
            assertThrows(IllegalStateException.class, () -> raceConditionService.runRaceConditionDemo(4, 1000));
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void raceConditionDemoCanFinishWithoutDetectedRaceForSingleThread() {
        RaceConditionDemoResponse result = raceConditionService.runRaceConditionDemo(1, 1000);

        assertEquals(result.getExpected(), result.getUnsafeCounter());
        assertTrue(result.getAttempts() >= 1 && result.getAttempts() <= 3);
    }
}
