package org.spribe.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import static org.spribe.utils.rest.RestManager.validateResponseBody;

@Log4j2
public class JsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setVisibility(MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    @SneakyThrows
    public static <T> T map(Response response, Class<T> clazz) {
        return map(validateResponseBody(response).getBody().asString(), clazz);
    }

    @SneakyThrows
    public static <T> T map(String json, Class<T> clazz) {
        return MAPPER.readValue(json, clazz);
    }

    @SneakyThrows
    public static <T> T map(Response response, TypeReference<T> typeReference) {
        return map(validateResponseBody(response).getBody().asString(), typeReference);
    }

    @SneakyThrows
    public static <T> T map(String json, TypeReference<T> typeReference) {
        return MAPPER.readValue(json, typeReference);
    }
}
