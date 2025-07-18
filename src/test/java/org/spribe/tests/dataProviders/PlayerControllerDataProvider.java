package org.spribe.tests.dataProviders;

import org.spribe.constants.URL;
import org.spribe.dto.PlayerDTO;
import org.spribe.utils.rest.RestManager;
import org.testng.annotations.DataProvider;

public class PlayerControllerDataProvider {

    @DataProvider
    public static Object[] createPlayerNegativeDataProvider() {
        var playerDTO = PlayerDTO.builder()
                .age(5)
                .gender("gender")
                .login("login")
                .role("role")
                .screenName("screenName")
                .build();
        return new Object[][]{
                {
                        RestManager.given()
                                .basePath(URL.CREATE_PLAYER)
                                .pathParams("editor", "")
                                .queryParams("age", playerDTO.getAge().toString(),
                                "gender", playerDTO.getGender(),
                                "login", playerDTO.getLogin(),
                                "role", playerDTO.getRole(),
                                "screenName", playerDTO.getScreenName())
                },
                {
                        RestManager.given()
                                .basePath(URL.CREATE_PLAYER)
                                .pathParams("editor", "editor")
                                .queryParams("age", null,
                                "gender", playerDTO.getGender(),
                                "login", playerDTO.getLogin(),
                                "role", playerDTO.getRole(),
                                "screenName", playerDTO.getScreenName())
                },
                {
                        RestManager.given()
                                .basePath(URL.CREATE_PLAYER)
                                .pathParams("editor", "editor")
                                .queryParams("age", playerDTO.getAge().toString(),
                                "gender", null,
                                "login", playerDTO.getLogin(),
                                "role", playerDTO.getRole(),
                                "screenName", playerDTO.getScreenName())
                },
                {
                        RestManager.given()
                                .basePath(URL.CREATE_PLAYER)
                                .pathParams("editor", "editor")
                                .queryParams("age", playerDTO.getAge().toString(),
                                "gender", playerDTO.getGender(),
                                "login", playerDTO.getLogin(),
                                "role", null,
                                "screenName", playerDTO.getScreenName())
                },
                {
                        RestManager.given()
                                .basePath(URL.CREATE_PLAYER)
                                .pathParams("editor", "editor")
                                .queryParams("age", playerDTO.getAge().toString(),
                                "gender", playerDTO.getGender(),
                                "login", playerDTO.getLogin(),
                                "role", playerDTO.getRole(),
                                "screenName", null)
                }
        };
    }

    @DataProvider
    public static Object[] deletePlayerNegativeDataProvider() {
        return new Object[][]{
                {
                        RestManager.given()
                                .basePath(URL.DELETE_PLAYER)
                                .pathParams("editor", null)
                                .body(PlayerDTO.builder().id(0L).build())
                },
                {
                        RestManager.given()
                                .basePath(URL.DELETE_PLAYER)
                                .pathParams("editor", "editor")
                                .body(PlayerDTO.builder().id(null).build())
                }
        };
    }

    @DataProvider
    public static Object[] getPlayerByIdNegativeDataProvider() {
        return new Object[][]{
                {
                        RestManager.given()
                                .basePath(URL.GET_PLAYER_BY_ID)
                                .body(PlayerDTO.builder().id(null).build())
                },
                {
                        RestManager.given()
                                .basePath(URL.GET_PLAYER_BY_ID)
                }
        };
    }
}
