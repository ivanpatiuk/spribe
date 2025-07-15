package org.spribe.utils.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.response.Response;
import org.spribe.utils.JsonMapper;
import org.testng.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class AssertUtil {

    public static void validateSchema(Object testClass, Response response) {
        var jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        var jsonSchemaFilePath = Path.of(String.format("src/test/resources/schemas/%s.json", getCallerTestMethodName(testClass)));
        try {
            var jsonSchemaFile = Files.readString(jsonSchemaFilePath);
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
