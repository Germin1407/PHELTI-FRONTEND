package com.phelti.frontend.Dtos;

import jakarta.validation.constraints.NotBlank;
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
public class PublicationRequest {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer categoryId;

    @NotBlank
    private String publicationType;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 4000)
    private String description;

    @NotBlank
    private String interactionMode;

    @NotNull
    @Positive
    private BigDecimal pheltAmount;

    private boolean negotiable;

    private String publicationStatus;

    @NotNull
    @Positive
    private Integer durationMinutes;
}
