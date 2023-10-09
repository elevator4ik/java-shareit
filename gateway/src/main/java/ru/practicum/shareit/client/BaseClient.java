package ru.practicum.shareit.client;

import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class BaseClient {
    protected final RestTemplate rest;

    protected ResponseEntity<Object> get(String path, Integer userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path,
                                              Integer userId,
                                              T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path,
                                               Integer userId,
                                               T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path,
                                               Integer userId,
                                               Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, null);
    }

    protected void delete(String path, Integer userId) {
        makeAndSendRequest(HttpMethod.DELETE, path, userId, null, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          Integer userId,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}