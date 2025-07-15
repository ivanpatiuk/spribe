package org.spribe.tests.api;

import org.spribe.utils.rest.RestManager;
import org.testng.annotations.Test;


public class GetPlayerControllerTests extends BaseTest {

    private final String getAllPlayersUrl = "/player/get/all";

    @Test
    void getAllPlayersTest()  {
        var response = RestManager.given()
                .get(getAllPlayersUrl)
                .then()
                .statusCode(200)
                .extract()
                .response();
        validateSchema(response);
    }
}
