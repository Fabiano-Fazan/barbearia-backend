package com.barbearia.scheduling.domain.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public record TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
    public TimeSlot {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start must be before end");
        }
    }

    public boolean overlaps(TimeSlot other) {
        return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
    }
}
