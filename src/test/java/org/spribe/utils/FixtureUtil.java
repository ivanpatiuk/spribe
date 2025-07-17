package org.spribe.utils;

import org.spribe.constants.URL;
import org.spribe.dto.PlayerDTO;
import org.spribe.utils.assertion.AssertUtil;
import org.spribe.utils.rest.RestManager;

import static org.testng.Assert.assertTrue;

public class FixtureUtil {

    public static Long createPlayer(String editor, PlayerDTO playerDTO) {
        var response = RestManager.given()
                .basePath(URL.CREATE_PLAYER)
                .pathParams("editor", editor)
                .queryParams("age", playerDTO.getAge().toString(),
                        "gender", playerDTO.getGender(),
                        "login", playerDTO.getLogin(),
                        "role", playerDTO.getRole(),
                        "screenName", playerDTO.getScreenName())
                // In the spribe docs the method for creating a player is GET, which is incorrect by REST contract.
                // In this method POST was used instead of GET for emphasizing on correct approach. In real life
                // this would be reported as a bug.
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();
        var playerDTOFromResponse = response.as(PlayerDTO.class);
        var id = playerDTO.getId();
        assertTrue(id >= 0);
        AssertUtil.assertPlayersEqual(playerDTO, playerDTOFromResponse);
        return id;
    }

    public static void deletePlayer(String editor, Long id) {
        RestManager.given()
                .basePath(URL.DELETE_PLAYER)
                .pathParams("editor", editor)
                .body(PlayerDTO.builder().id(id).build())
                .then()
                .statusCode(200)
                .response();
    }
}
