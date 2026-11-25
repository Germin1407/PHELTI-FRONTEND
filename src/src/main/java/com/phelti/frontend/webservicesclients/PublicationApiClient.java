package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PublicationApiClient extends BaseApiClient {
    public PublicationApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<PublicationResponse> listar() { return getList("/publications", PublicationResponse[].class); }
    public PublicationResponse obtener(Integer id) { return get("/publications/{id}", PublicationResponse.class, id); }
    public PublicationResponse crear(PublicationRequest datos) {
        return post("/publications", datos, PublicationResponse.class);
    }
    public PublicationResponse actualizar(Integer id, PublicationRequest datos) {
        return put("/publications/{id}", datos, PublicationResponse.class, id);
    }
    public void eliminar(Integer id) { delete("/publications/{id}", id); }
    public List<PublicationResponse> buscar(String publicationType, String interactionMode,
            Integer categoryId, String keyword, BigDecimal maxPheltAmount, String publicationStatus) {
        var uri = UriComponentsBuilder.fromUriString(baseUrl + "/publications/search");
        if (publicationType != null && !publicationType.isBlank()) uri.queryParam("publicationType", publicationType);
        if (interactionMode != null && !interactionMode.isBlank()) uri.queryParam("interactionMode", interactionMode);
        if (categoryId != null) uri.queryParam("categoryId", categoryId);
        if (keyword != null && !keyword.isBlank()) uri.queryParam("keyword", keyword);
        if (maxPheltAmount != null) uri.queryParam("maxPheltAmount", maxPheltAmount);
        if (publicationStatus != null && !publicationStatus.isBlank()) uri.queryParam("publicationStatus", publicationStatus);
        return getList(uri.build().encode().toUri(), PublicationResponse[].class);
    }
}
