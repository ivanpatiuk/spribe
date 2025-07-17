package org.spribe.tests.api;

import io.restassured.response.Response;
import org.spribe.enums.ValidationRule;
import org.spribe.utils.allure.AllureListener;
import org.spribe.utils.assertion.AssertUtil;
import org.testng.annotations.Listeners;

import java.util.Map;

@Listeners(AllureListener.class)
public class BaseTest {

    private Response response;

    protected BaseTest validate(Response response) {
        this.response = response;
        return this;
    }

    protected BaseTest withSchema() {
        AssertUtil.validateSchema(this, response);
        return this;
    }

    protected void withValidationRules(Map<String, ValidationRule> fieldRuleMap) {
        AssertUtil.validateResponse(response, fieldRuleMap);
    }
}
