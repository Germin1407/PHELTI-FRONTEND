package com.phelti.frontend.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 1500)
    private String description;

    @Size(max = 150)
    private String location;

    private String preferredInteractionMode;
}
