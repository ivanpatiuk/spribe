package org.spribe.tests.api;

import io.restassured.response.Response;
import org.spribe.utils.allure.AllureListener;
import org.spribe.utils.assertion.AssertUtil;
import org.testng.annotations.Listeners;

@Listeners(AllureListener.class)
public class BaseTest {

    protected void validateSchema(Response response) {
        AssertUtil.validateSchema(this, response);
    }
}
