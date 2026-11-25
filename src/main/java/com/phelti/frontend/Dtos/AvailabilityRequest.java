package com.phelti.frontend.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequest {
    @NotBlank
    private String dayOfWeek;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
