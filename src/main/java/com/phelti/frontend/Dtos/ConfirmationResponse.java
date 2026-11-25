package com.phelti.frontend.Dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationResponse {
    private Integer confirmationId;

    private Integer activityId;

    private Integer userId;

    private LocalDateTime confirmedAt;
}
