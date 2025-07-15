package org.spribe.utils.rest;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.spribe.utils.allure.AllureLogger;

import java.util.Map;

@Log4j2
public class RestAssuredRequestFilter implements Filter {

    public static String getRequestDetails(@NonNull final FilterableRequestSpecification requestSpec) {
        return String.format("Request method: %s, Request URI: %s, Request body: %s",
                requestSpec.getMethod(),
                requestSpec.getURI(),
                requestSpec.getBody());
    }

    public static String getResponseDetails(@NonNull final Response response) {
        return String.format("Response status: %s, Response status line: %s, Response headers: %s, Response body: %s",
                response.getStatusCode(),
                response.getStatusLine(),
                response.getHeaders(),
                response.getBody().asString());
    }


    @Override
    public Response filter(final FilterableRequestSpecification requestSpec, final FilterableResponseSpecification responseSpec, final FilterContext ctx) {
        var response = ctx.next(requestSpec, responseSpec);
        var requestDetails = getRequestDetails(requestSpec);
        var responseDetails = getResponseDetails(response);
        log.debug(requestDetails);
        log.debug(responseDetails);
        AllureLogger.addTestCaseParameters(Map.of("Request details", requestDetails, "Response details", responseDetails));
        return response;
    }
}
