package com.phelti.frontend.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {
    @NotNull
    private Integer proposalId;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate activityDate;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime activityTime;

    @NotNull
    @Positive
    private Integer durationMinutes;

    @Size(max = 200)
    private String location;

    @Size(max = 300)
    private String virtualLink;
}
