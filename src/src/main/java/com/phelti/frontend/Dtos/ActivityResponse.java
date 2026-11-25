package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private Integer activityId;

    private Integer publicationId;

    private Integer proposalId;

    private Integer requesterId;

    private Integer participantId;

    private String interactionMode;

    private LocalDate activityDate;

    private LocalTime activityTime;

    private Integer durationMinutes;

    private String location;

    private String virtualLink;

    private BigDecimal agreedAmount;

    private String checkInCode;

    private String activityStatus;
}
