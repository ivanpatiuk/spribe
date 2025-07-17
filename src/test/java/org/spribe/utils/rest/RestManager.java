package org.spribe.utils.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestManager {

    private static final String BASE_URI;
    private static final RequestSpecification REQUEST_SPECIFICATION;

    static {
        var baseUriProperty = System.getProperty("baseUri");
        if (baseUriProperty != null && !baseUriProperty.isBlank()) {
            BASE_URI = baseUriProperty;
        } else {
            BASE_URI = "http://3.68.165.45/player";
        }
        REQUEST_SPECIFICATION = RestAssured.given()
                .baseUri(BASE_URI)
                .filter(new RestAssuredRequestFilter())
                .contentType(ContentType.JSON);
    }

    public static String getBaseUri() {
        return BASE_URI;
    }

    public static RequestSpecification given() {
        return REQUEST_SPECIFICATION;
    }

    public static Response validateResponseBody(Response response) {
        if (response.getBody() == null) {
            throw new NullPointerException("Response body is null");
        }
        return response;
    }
}
