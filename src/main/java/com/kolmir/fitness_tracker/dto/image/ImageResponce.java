package com.kolmir.fitness_tracker.dto.image;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImageResponce {
    private String filename;
    private String path;
    private Long ownerId;
}
