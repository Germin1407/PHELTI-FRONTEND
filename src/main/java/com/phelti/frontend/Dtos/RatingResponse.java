package com.phelti.frontend.Dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Integer ratingId;

    private Integer activityId;

    private Integer raterId;

    private Integer ratedId;

    private Integer score;

    private String comment;

    private LocalDateTime createdAt;
}
