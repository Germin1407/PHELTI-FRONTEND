package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class ProposalApiClient extends BaseApiClient {
    public ProposalApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<ProposalResponse> listar() { return getList("/proposals", ProposalResponse[].class); }
    public ProposalResponse obtener(Integer id) { return get("/proposals/{id}", ProposalResponse.class, id); }
    public ProposalResponse crear(ProposalRequest datos) { return post("/proposals", datos, ProposalResponse.class); }
    public ProposalResponse aceptar(Integer id) {
        return put("/proposals/{id}/accept", null, ProposalResponse.class, id);
    }
    public ProposalResponse rechazar(Integer id) {
        return put("/proposals/{id}/reject", null, ProposalResponse.class, id);
    }
    public void eliminar(Integer id) { delete("/proposals/{id}", id); }
}
