package com.phelti.frontend.Dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationMatchResponse {
    private PublicationResponse publication;

    private int score;

    private List<String> reasons;
}
