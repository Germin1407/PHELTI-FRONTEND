package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class SkillApiClient extends BaseApiClient {
    public SkillApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<SkillResponse> listarCatalogo() { return getList("/skills", SkillResponse[].class); }
    public List<SkillResponse> deUsuario(Integer userId) {
        return getList("/users/{id}/skills", SkillResponse[].class, userId);
    }
    public void asignar(Integer userId, Integer skillId) {
        put("/users/{userId}/skills/{skillId}", null, Void.class, userId, skillId);
    }
    public void quitar(Integer userId, Integer skillId) {
        delete("/users/{userId}/skills/{skillId}", userId, skillId);
    }
}
