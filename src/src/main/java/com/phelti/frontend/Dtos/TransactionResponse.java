package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Integer transactionId;

    private String transactionType;

    private BigDecimal amount;

    private String description;

    private LocalDateTime createdAt;

    private Integer activityId;

    private Integer originUserId;

    private Integer destinationUserId;
}
