package org.spribe.tests.api;

import org.spribe.constants.Group;
import org.spribe.constants.URL;
import org.spribe.dto.PlayerDTO;
import org.spribe.utils.FixtureUtil;
import org.spribe.utils.rest.RestManager;
import org.testng.annotations.Test;

import java.util.Map;

import static org.spribe.enums.ValidationRule.UNIQUE;
import static org.spribe.utils.assertion.AssertUtil.assertPlayersEqual;
import static org.spribe.utils.assertion.AssertUtil.validate;
import static org.testng.Assert.assertEquals;

public class PlayerControllerTests extends BaseTest {

    private Long id = null;
    private final PlayerDTO testPlayerDTO = PlayerDTO.builder()
            .age(5)
            .gender("gender")
            .login("login")
            .role("role")
            .screenName("screenName")
            .build();

    @Test(groups = Group.FIXTURE)
    void createPlayerPositiveTest() {
        id = FixtureUtil.createPlayer("editor1", testPlayerDTO);
    }

    @Test(groups = Group.FIXTURE, dependsOnMethods = "createPlayerPositiveTest")
    void getPlayerByIdPositiveTest() {
        var response = RestManager.given()
                .basePath(URL.GET_PLAYER_BY_ID)
                .body(PlayerDTO.builder().id(0L).build())
                // In the spribe docs the method for getting a player is POST, which is incorrect by REST contract.
                // In this method GET was used instead of POST for emphasizing on correct approach. In real life
                // this would be reported as a bug.
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        var playerDTO = response.as(PlayerDTO.class);
        assertEquals(playerDTO.getId(), id);
        assertPlayersEqual(testPlayerDTO, playerDTO);
    }

    @Test(groups = Group.FIXTURE, dependsOnMethods = "getPlayerByIdPositiveTest")
    void deletePlayerTest() {
        FixtureUtil.deletePlayer("editor1", id);
    }

    @Test(dependsOnGroups = Group.FIXTURE)
    void getAllPlayersPositiveTest() {
        var response = RestManager.given()
                .get(URL.GET_ALL_PLAYERS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        validate(response)
                .withSchema()
                .withValidationRules(Map.of(
                        "id", UNIQUE));
    }

    @Test
    void getAllPlayersNegativeTest() {
        RestManager.given()
                .get(URL.GET_ALL_PLAYERS + "/1")
                .then()
                .statusCode(404)
                .extract()
                .response();
    }
}
