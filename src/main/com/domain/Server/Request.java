package main.com.domain.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Request {

    private final HttpExchange httpExchange;
    private Headers headers;
    private String rawBody;

    private String jsonBody;

    public Request(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
         this.headers = httpExchange.getRequestHeaders();
    }

    public String getBody() {
    if (this.rawBody == null) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))) {
            this.rawBody = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return this.rawBody;
}

    public String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    public String getContentType() {
        return headers.getFirst("Content-Type");
    }

    public Map<String, Object> getJSON() throws JsonProcessingException {
        if (!getContentType().equalsIgnoreCase("application/json")) {
            return null;
        }

        Map<String, Object> jsonMap = new HashMap<>();
        if (jsonBody == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonMap = objectMapper.readValue(this.getBody(), new TypeReference<>(){});
        }

        return jsonMap;
    }


}
