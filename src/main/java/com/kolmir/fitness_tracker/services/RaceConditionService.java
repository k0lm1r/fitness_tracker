package com.kolmir.fitness_tracker.services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.kolmir.fitness_tracker.dto.exercise.RaceConditionDemoResponse;

@Service
public class RaceConditionService {

    public RaceConditionDemoResponse runRaceConditionDemo() {
        return runRaceConditionDemo(64, 250_000);
    }

    public RaceConditionDemoResponse runRaceConditionDemo(int threadCount, int incrementsPerThread) {
        int expected = threadCount * incrementsPerThread;
        int[] attemptResult = {expected, expected};
        int attempts = 0;

        while (attempts < 3 && attemptResult[0] == expected) {
            attempts++;
            attemptResult = runRaceAttempt(threadCount, incrementsPerThread);
        }

        int lostUpdates = expected - attemptResult[0];
        return new RaceConditionDemoResponse(
                threadCount,
                incrementsPerThread,
                expected,
                attemptResult[0],
                attemptResult[1],
                Math.max(lostUpdates, 0),
                attemptResult[0] != expected,
                attempts);
    }

    private int[] runRaceAttempt(int threadCount, int incrementsPerThread) {
        int[] unsafeCounter = {0};
        AtomicInteger atomicCounter = new AtomicInteger(0);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(50, threadCount));

        try {
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        startLatch.await();
                        for (int j = 0; j < incrementsPerThread; j++) {
                            int current = unsafeCounter[0];
                            if ((j & 63) == 0)
                                Thread.yield();
                            unsafeCounter[0] = current + 1;
                            atomicCounter.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("выполнение гонки прервано", e);
        } finally {
            executorService.shutdownNow();
        }

        return new int[] {unsafeCounter[0], atomicCounter.get()};
    }
}
