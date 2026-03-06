package com.kolmir.fitness_tracker.dto.day;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class DayResponse {
    private LocalDate date;
    private Long wokroutId;
    private Long ownerId;
    private Integer calories;
}
