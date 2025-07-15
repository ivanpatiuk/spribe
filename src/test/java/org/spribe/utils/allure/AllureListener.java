package org.spribe.utils.allure;

import io.qameta.allure.testng.AllureTestNg;
import lombok.extern.log4j.Log4j2;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;

@Log4j2
public class AllureListener extends AllureTestNg {

    @Override
    public void onTestStart(ITestResult testResult) {
        log.info("Running test method: {}", testResult.getMethod().getQualifiedName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        log.info("Finished executing test method: {}", testResult.getMethod().getQualifiedName());
    }

    @Override
    public void onTestFailure(ITestResult testResult) {
        log.error("The test method: {} has failed", testResult.getMethod().getQualifiedName());
    }
}
