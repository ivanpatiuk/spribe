package org.spribe.tests.api;

import org.spribe.utils.rest.RestManager;
import org.testng.annotations.Test;
import java.util.Map;

import static org.spribe.enums.ValidationRule.UNIQUE;


public class GetPlayerControllerTests extends BaseTest {

    @Test
    void getAllPlayersTest() {
        var getAllPlayersUrl = "/player/get/all";
        var response = RestManager.given()
                .get(getAllPlayersUrl)
                .then()
                .statusCode(200)
                .extract()
                .response();

        validate(response)
                .withSchema()
                .withValidationRules(Map.of(
                        "id", UNIQUE));
    }
}
