package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class InterestApiClient extends BaseApiClient {
    public InterestApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<InterestResponse> listarCatalogo() { return getList("/interests", InterestResponse[].class); }
    public List<InterestResponse> deUsuario(Integer userId) {
        return getList("/users/{id}/interests", InterestResponse[].class, userId);
    }
    public void asignar(Integer userId, Integer interestId) {
        put("/users/{userId}/interests/{interestId}", null, Void.class, userId, interestId);
    }
    public void quitar(Integer userId, Integer interestId) {
        delete("/users/{userId}/interests/{interestId}", userId, interestId);
    }
}
