package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Component
public class MetricsApiClient extends BaseApiClient {
    public MetricsApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public UserMetricsResponse obtener(Integer userId) {
        return get("/users/{id}/metrics", UserMetricsResponse.class, userId);
    }
}
