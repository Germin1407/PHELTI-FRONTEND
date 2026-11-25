package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.ErrorResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class BaseApiClient {
    protected final RestTemplate restTemplate;
    protected final String baseUrl;
    private final JsonMapper jsonMapper;

    protected BaseApiClient(RestTemplate restTemplate, JsonMapper jsonMapper, String baseUrl) {
        this.restTemplate = restTemplate;
        this.jsonMapper = jsonMapper;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    protected <T> T get(String path, Class<T> type, Object... uriVariables) {
        return execute(() -> restTemplate.getForObject(baseUrl + path, type, uriVariables));
    }

    protected <T> List<T> getList(String path, Class<T[]> type, Object... uriVariables) {
        T[] result = get(path, type, uriVariables);
        return result == null ? List.of() : Arrays.asList(result);
    }

    protected <T> List<T> getList(URI uri, Class<T[]> type) {
        T[] result = execute(() -> restTemplate.getForObject(uri, type));
        return result == null ? List.of() : Arrays.asList(result);
    }

    protected <T> T post(String path, Object request, Class<T> type, Object... uriVariables) {
        return execute(() -> restTemplate.postForObject(baseUrl + path, request, type, uriVariables));
    }

    protected <T> T put(String path, Object request, Class<T> type, Object... uriVariables) {
        return execute(() -> restTemplate.exchange(baseUrl + path, HttpMethod.PUT,
                new HttpEntity<>(request), type, uriVariables).getBody());
    }

    protected void delete(String path, Object... uriVariables) {
        execute(() -> restTemplate.exchange(baseUrl + path, HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, uriVariables));
    }

    protected <T> T execute(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (HttpStatusCodeException exception) {
            String message = "No se pudo completar la solicitud. Inténtalo nuevamente.";
            try {
                ErrorResponse error = jsonMapper.readValue(
                        exception.getResponseBodyAsString(), ErrorResponse.class);
                if (error != null && error.getMessage() != null && !error.getMessage().isBlank()) {
                    message = error.getMessage();
                }
            } catch (RuntimeException ignored) {

            }
            throw new BackendApiException(exception.getStatusCode().value(), message, exception);
        } catch (ResourceAccessException exception) {
            throw new BackendApiException(503,
                    "El servicio no está disponible en este momento. Inténtalo nuevamente más tarde.", exception);
        }
    }
}
