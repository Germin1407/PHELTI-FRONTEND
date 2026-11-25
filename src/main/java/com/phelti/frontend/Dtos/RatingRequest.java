package com.phelti.frontend.Dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    @NotNull
    private Integer activityId;

    @NotNull
    private Integer raterId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;

    @Size(max = 500)
    private String comment;
}
