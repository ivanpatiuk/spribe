package org.spribe.tests.api;

import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.spribe.constants.Group;
import org.spribe.constants.URL;
import org.spribe.dto.PlayerDTO;
import org.spribe.tests.dataProviders.PlayerControllerDataProvider;
import org.spribe.utils.FixtureUtil;
import org.spribe.utils.rest.RestManager;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.spribe.enums.ValidationRule.UNIQUE;
import static org.spribe.utils.assertion.AssertUtil.assertPlayersEqual;
import static org.spribe.utils.assertion.AssertUtil.validate;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class PlayerControllerTests extends BaseTest {

    private final PlayerDTO testPlayerDTO = PlayerDTO.builder()
            .age(5)
            .gender("gender")
            .login("login")
            .role("role")
            .screenName("screenName")
            .build();
    private final PlayerDTO updatePlayerDTO = PlayerDTO.builder()
            .age(55)
            .gender("gender5")
            .login("login5")
            .role("role5")
            .screenName("screenName5")
            .build();
    private final Supplier<Long> createFixtureFunction = () -> FixtureUtil.createPlayer("editor1", testPlayerDTO);
    private final Consumer<Long> deleteFixtureFunction = (createdPlayerId) -> FixtureUtil.deletePlayer("editor1", createdPlayerId);
    private Long id = null;

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
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        var playerDTO = response.as(PlayerDTO.class);
        assertEquals(playerDTO.getId(), id);
        assertPlayersEqual(testPlayerDTO, playerDTO);
    }

    @Test(groups = Group.FIXTURE, dependsOnMethods = "getPlayerByIdPositiveTest")
    void deletePlayerPositiveTest() {
        FixtureUtil.deletePlayer("editor1", id);
    }

    @Test(groups = Group.FIXTURE, dependsOnMethods = "deletePlayerPositiveTest")
    void getDeletedPlayerByIdPositiveTest() {
        var response = RestManager.given()
                .basePath(URL.GET_PLAYER_BY_ID)
                .body(PlayerDTO.builder().id(id).build())
                // In the spribe docs the method for getting a player is POST, which is incorrect by REST contract.
                // In this method GET was used instead of POST for emphasizing on correct approach. In real life
                // this would be reported as a bug.
                .get()
                .then()
                // or HttpStatus.SC_NOT_FOUND base on the underhood logic
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        var playerDTO = response.as(PlayerDTO.class);
        assertNull(playerDTO);
    }

    @Test(dataProviderClass = PlayerControllerDataProvider.class, dataProvider = "createPlayerNegativeDataProvider")
    void createPlayerNegativeTest(RequestSpecification requestSpecification) {
        requestSpecification
                .post()
                .then()
                // or another based on the logic under the hood
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(dataProviderClass = PlayerControllerDataProvider.class, dataProvider = "getPlayerByIdNegativeDataProvider")
    void getPlayerByIdNegativeTest(RequestSpecification requestSpecification) {
        requestSpecification
                // In the spribe docs the method for getting a player is POST, which is incorrect by REST contract.
                // In this method GET was used instead of POST for emphasizing on correct approach. In real life
                // this would be reported as a bug.
                .get()
                .then()
                // or HttpStatus.SC_BAD_REQUEST based on the logic under the hood
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract()
                .response();
    }

    @Test(dataProviderClass = PlayerControllerDataProvider.class, dataProvider = "deletePlayerNegativeDataProvider")
    void deletePlayerNegativeTest(RequestSpecification requestSpecification) {
        requestSpecification
                .post()
                .then()
                // or another based on the logic under the hood
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(dependsOnGroups = Group.FIXTURE)
    void getAllPlayersPositiveTest() {
        var response = RestManager.given()
                .get(URL.GET_ALL_PLAYERS)
                .then()
                .statusCode(HttpStatus.SC_OK)
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
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .extract()
                .response();
    }

    /*
        The test below might have more combinations of updated users that depend on the underhood logic. In this
        case mentioned 1 test with all fields, that can split on scenarios were user have updated some fields but not all.
     */
    @Test
    void updatePlayerPositiveTest() {
        validate(createFixtureFunction, deleteFixtureFunction, (createdPlayerId) -> {
            var response = RestManager.given()
                    .basePath(URL.UPDATE_PLAYER)
                    .pathParams("editor", "editor1",
                            "id", createdPlayerId)
                    .body(updatePlayerDTO)
                    .patch()
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .response();
            var playerDTO = response.as(PlayerDTO.class);
            assertEquals(createdPlayerId, playerDTO.getId());
            assertPlayersEqual(updatePlayerDTO, playerDTO);

            // extra validation with a GET request
            response = RestManager.given()
                    .basePath(URL.GET_PLAYER_BY_ID)
                    .body(PlayerDTO.builder().id(createdPlayerId).build())
                    .get()
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .response();
            playerDTO = response.as(PlayerDTO.class);
            assertEquals(createdPlayerId, playerDTO.getId());
            assertPlayersEqual(testPlayerDTO, playerDTO);
        });
    }

    @Test
    void updatePlayerNullBodyNegativeTest() {
        validate(createFixtureFunction, deleteFixtureFunction, (createdPlayerId) -> RestManager.given()
                .basePath(URL.UPDATE_PLAYER)
                .pathParams("editor", "editor1",
                        "id", createdPlayerId)
                .patch()
                .then()
                // or another based on the logic under the hood
                .statusCode(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    void updatePlayerNullEditorNegativeTest() {
        validate(createFixtureFunction, deleteFixtureFunction, (createdPlayerId) -> RestManager.given()
                .basePath(URL.UPDATE_PLAYER)
                .pathParams("editor", null,
                        "id", createdPlayerId)
                .body(updatePlayerDTO)
                .patch()
                .then()
                // or another based on the logic under the hood
                .statusCode(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    void updatePlayerNullIdNegativeTest() {
        validate(createFixtureFunction, deleteFixtureFunction, (createdPlayerId) -> RestManager.given()
                .basePath(URL.UPDATE_PLAYER)
                .pathParams("editor", "editor1",
                        "id", null)
                .body(updatePlayerDTO)
                .patch()
                .then()
                // or another based on the logic under the hood
                .statusCode(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    void updatePlayerNonExistentIdNegativeTest() {
        validate(createFixtureFunction, deleteFixtureFunction, (createdPlayerId) -> RestManager.given()
                .basePath(URL.UPDATE_PLAYER)
                .pathParams("editor", "editor1",
                        "id", 999999999L)
                .body(updatePlayerDTO)
                .patch()
                .then()
                // or another based on the logic under the hood
                .statusCode(HttpStatus.SC_NOT_FOUND));
    }
}
