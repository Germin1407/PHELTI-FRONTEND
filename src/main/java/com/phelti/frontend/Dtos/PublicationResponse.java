package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationResponse {
    private Integer publicationId;

    private Integer userId;

    private Integer categoryId;

    private String publicationType;

    private String title;

    private String description;

    private String interactionMode;

    private BigDecimal pheltAmount;

    private boolean negotiable;

    private String publicationStatus;

    private Integer durationMinutes;
}
