package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class RatingApiClient extends BaseApiClient {
    public RatingApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public RatingResponse crear(RatingRequest datos) { return post("/ratings", datos, RatingResponse.class); }
    public List<RatingResponse> recibidas(Integer userId) {
        return getList("/ratings/users/{id}", RatingResponse[].class, userId);
    }
    public List<RatingResponse> deActividad(Integer activityId) {
        return getList("/ratings/activities/{id}", RatingResponse[].class, activityId);
    }
}
