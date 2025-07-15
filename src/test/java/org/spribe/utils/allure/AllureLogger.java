package org.spribe.utils.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;

import java.util.Map;

public class AllureLogger {

    public static void addTestCaseParameters(final Map<String, String> parameters) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            var testCaseParameters = testResult.getParameters();
            parameters
                    .forEach((name, value) -> testCaseParameters
                            .add(new Parameter()
                                    .setName(name)
                                    .setValue(value)));
        });
    }

    public static void clearStepParameters() {
        Allure.getLifecycle().updateStep(stepResult -> stepResult.setParameters(null));
    }
}

