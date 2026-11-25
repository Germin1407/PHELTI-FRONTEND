package com.phelti.frontend.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalRequest {
    @NotNull
    private Integer publicationId;

    @NotNull
    private Integer proposingUserId;

    @Size(max = 500)
    private String message;

    @Positive
    private BigDecimal proposedAmount;
}
