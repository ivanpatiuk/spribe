package org.spribe.utils.assertion;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.spribe.dto.PlayerDTO;
import org.spribe.enums.ValidationRule;
import org.spribe.utils.JsonMapper;
import org.spribe.utils.allure.AllureLogger;
import org.testng.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;

@AllArgsConstructor
@Log4j2
public class AssertUtil {

    private Response response;

    public static AssertUtil validate(Response response) {
        return new AssertUtil(response);
    }

    public static void assertPlayersEqual(PlayerDTO expected, PlayerDTO actual) {
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getLogin(), actual.getGender());
        assertEquals(expected.getRole(), actual.getGender());
        assertEquals(expected.getScreenName(), actual.getGender());
    }

    public void validate(RequestSpecification createFixtureRequest, RequestSpecification deleteFixtureRequest, Runnable assertFunction) {
        Integer id = null;
        try {
            try {
                id = createFixtureRequest
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response()
                        .jsonPath()
                        .get("$.id");
                if (id == null) {
                    throw new IllegalArgumentException("Id was null for created player");
                }
            } catch (Exception e) {
                log.fatal("Unable to create fixture", e);
            }
            assertFunction.run();
        } finally {
            if (id != null) {
                deleteFixtureRequest.delete().then().statusCode(200);
            }
        }
    }

    public AssertUtil withSchema() {
        AssertUtil.validateSchema(this, response);
        return this;
    }

    public void withValidationRules(Map<String, ValidationRule> fieldRuleMap) {
        AssertUtil.validateResponse(response, fieldRuleMap);
    }

    @Step("Validate schema for {0}")
    public static void validateSchema(Object testClass, Response response) {
        AllureLogger.clearStepParameters();
        var jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        var jsonSchemaFilePath = Path.of(String.format("src/test/resources/schemas/%s.json", getCallerTestMethodName(testClass)));
        try {
            var jsonSchemaFile = Files.readString(jsonSchemaFilePath);
            AllureLogger.addTestCaseParameters(Map.of("Schema", jsonSchemaFile));
            var jsonSchema = jsonSchemaFactory.getSchema(jsonSchemaFile);
            var jsonNode = JsonMapper.readTree(response);
            var errors = jsonSchema.validate(jsonNode);
            if (!errors.isEmpty()) {
                Assert.fail(getErrorsDetails(errors));
            }
        } catch (Exception e) {
            throw new RuntimeException("Schema file not found by the next path: " + jsonSchemaFilePath);
        }
    }

    @Step("Validate response for {0}")
    public static void validateResponse(Response response, Map<String, ValidationRule> fieldRuleMap) {
        AllureLogger.clearStepParameters();
        var responseBody = response.getBody().asString();
        fieldRuleMap.forEach((field, rule) -> rule.getFunction().accept(responseBody, field));
    }

    private static String getErrorsDetails(Set<ValidationMessage> errors) {
        return errors.stream()
                .map(error -> String.format("Error code: %s, Error message: %s, Error details: %s", error.getCode(), error.getMessage(), error.getDetails()))
                .reduce("", (acc, error) -> acc + "\n" + error);
    }

    private static String getCallerTestMethodName(Object ignored) {
        var stackTrace = Thread.currentThread().getStackTrace();
        for (var caller : stackTrace) {
            var methodName = caller.getMethodName();
            if (methodName.endsWith("Test")) {
                return methodName.replace("Test", "Schema");
            }
        }
        throw new RuntimeException("Unable to get caller test method name");
    }
}
