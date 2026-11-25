package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMetricsResponse {
    private Integer userId;

    private long totalActivities;

    private long scheduledActivities;

    private long completedActivities;

    private long cancelledActivities;

    private BigDecimal completionRate;

    private BigDecimal averageRating;

    private long ratingsReceived;

    private BigDecimal pheltsEarned;

    private BigDecimal pheltsSpent;
}
