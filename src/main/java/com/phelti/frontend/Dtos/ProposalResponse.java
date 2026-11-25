package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalResponse {
    private Integer proposalId;

    private Integer publicationId;

    private Integer proposingUserId;

    private String message;

    private BigDecimal proposedAmount;

    private String proposalStatus;

    private Instant createdAt;
}
