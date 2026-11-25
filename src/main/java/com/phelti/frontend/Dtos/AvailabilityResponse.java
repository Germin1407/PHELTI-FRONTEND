package com.phelti.frontend.Dtos;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private Integer availabilityId;

    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}
