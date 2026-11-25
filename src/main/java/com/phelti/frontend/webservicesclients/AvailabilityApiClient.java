package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class AvailabilityApiClient extends BaseApiClient {
    public AvailabilityApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<AvailabilityResponse> deUsuario(Integer userId) {
        return getList("/users/{id}/availability", AvailabilityResponse[].class, userId);
    }
    public AvailabilityResponse agregar(Integer userId, AvailabilityRequest datos) {
        return post("/users/{id}/availability", datos, AvailabilityResponse.class, userId);
    }
    public void eliminar(Integer availabilityId) { delete("/availability/{id}", availabilityId); }
}
