package com.phelti.frontend.Dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer userId;

    private String name;

    private String email;

    private String description;

    private BigDecimal averageRating;

    private String location;

    private String preferredInteractionMode;
}
