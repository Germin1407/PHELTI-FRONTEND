package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class ActivityApiClient extends BaseApiClient {
    public ActivityApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public List<ScheduleOfferResponse> horarios(Integer proposalId) { return getList("/activities/schedule/{id}", ScheduleOfferResponse[].class, proposalId); }
    public ScheduleOfferResponse proponerHorario(ActivityRequest datos) { return post("/activities/schedule", datos, ScheduleOfferResponse.class); }
    public ActivityResponse aceptarHorario(Integer id) { return put("/activities/schedule/{id}/accept", null, ActivityResponse.class, id); }
    public ScheduleOfferResponse rechazarHorario(Integer id) { return put("/activities/schedule/{id}/decline", null, ScheduleOfferResponse.class, id); }
    public void cancelarAcuerdo(Integer id) { put("/activities/schedule/proposals/{id}/cancel", null, Void.class, id); }
    public ActivityResponse obtener(Integer id) { return get("/activities/{id}", ActivityResponse.class, id); }
    public List<ActivityResponse> deUsuario(Integer userId) {
        return getList("/activities/users/{userId}", ActivityResponse[].class, userId);
    }
    public ActivityResponse confirmar(Integer id, ConfirmationRequest datos) {
        return post("/activities/{id}/confirmations", datos, ActivityResponse.class, id);
    }
    public List<ConfirmationResponse> confirmaciones(Integer id) {
        return getList("/activities/{id}/confirmations", ConfirmationResponse[].class, id);
    }
    public ActivityResponse cancelar(Integer id) {
        return put("/activities/{id}/cancel", null, ActivityResponse.class, id);
    }
}
