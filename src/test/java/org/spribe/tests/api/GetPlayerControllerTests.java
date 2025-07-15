package org.spribe.tests.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.spribe.dto.PlayerDTO;
import org.spribe.utils.JsonMapper;
import org.spribe.utils.rest.RestManager;
import org.testng.annotations.Test;

import java.util.List;

public class GetPlayerControllerTests {

    private final String getAllPlayersUrl = "/player/get/all";

    // todo, try with schema and without, schema not tested
    @Test
    void getAllPlayersTest() {
        var response = RestManager.given()
                .get(getAllPlayersUrl)
                .then()
                .statusCode(200)
                .extract()
                .response();
        var playerItemList = JsonMapper.map(response, new TypeReference<List<PlayerDTO>>(){});
    }
}
