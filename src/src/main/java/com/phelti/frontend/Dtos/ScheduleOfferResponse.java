package com.phelti.frontend.Dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleOfferResponse {
    private Integer scheduleId;
    private Integer proposalId;
    private Integer requesterId;
    private LocalDate activityDate;
    private LocalTime activityTime;
    private Integer durationMinutes;
    private String location;
    private String virtualLink;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
}
