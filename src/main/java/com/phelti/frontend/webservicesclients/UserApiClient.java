package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UserApiClient extends BaseApiClient {
    public UserApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<UserResponse> listar() { return getList("/users", UserResponse[].class); }
    public UserResponse obtener(Integer id) { return get("/users/{id}", UserResponse.class, id); }
    public UserResponse registrar(UserRequest datos) { return post("/users", datos, UserResponse.class); }
    public UserResponse actualizar(Integer id, ProfileUpdateRequest datos) {
        return put("/users/{id}", datos, UserResponse.class, id);
    }
    public List<UserResponse> buscar(Integer skillId, Integer interestId, String interactionMode, String dayOfWeek) {
        var uri = UriComponentsBuilder.fromUriString(baseUrl + "/users/search");
        if (skillId != null) uri.queryParam("skillId", skillId);
        if (interestId != null) uri.queryParam("interestId", interestId);
        if (interactionMode != null && !interactionMode.isBlank()) uri.queryParam("interactionMode", interactionMode);
        if (dayOfWeek != null && !dayOfWeek.isBlank()) uri.queryParam("dayOfWeek", dayOfWeek);
        return getList(uri.build().encode().toUri(), UserResponse[].class);
    }
}
