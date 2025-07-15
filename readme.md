# Spribe interview project

Interview project for API testing, with a focus on testing **player-controller**.

## Getting Started

This document will give you instructions about installing and running a project on local machine for testing purposes.
See implementation notes on how to configure and use the project.

### WARNING! The code examples in this document don't represent all functionality of the described classes. Here are gathered only some examples or main methods.

## Prerequisites

Things you need to install and configure in order to use the project:

* [Intelij IDEA](https://www.jetbrains.com/idea/ "Intelij IDEA")
* [JDK 11](https://www.oracle.com/ua/java/technologies/javase/jdk11-archive-downloads.html "JDK 11")

In the scope of the project, there will be used the next main Maven dependencies:

* testng — the core of the automation framework
* rest-assured — the core of the API testing
* allure-testng — for generation Allure reports in integration with TestNG

The list is not final and contains only the most important framework parts.

## Running tests

### To run tests, you can use UI buttons in the IntelliJ IDEA, or run tests using the next maven command:

1. Run the test suite with the next maven command:

```
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/<suite>
```

where *"\<suite\>"* is one of the suites in the *"suites"* package, e.g.:

```
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/api-testng.xml
```

## Debugging and logging

### Logger

By default, the logger level is set to **info**, and only informational, warning, error and fatal messages would be
shown or saved into the log file.
The path places the configuration file for the logger:

```
\src\test\resources\log4j2.xml
```

### Logger level

To change the logger level, you need to change the logger's root level. Replace the logging level in the next line for "level":

```
<Logger name="org.spribe" level="debug"/>
```

### Logger output

As this automation framework supports concurrent tests executing, the logger is configured to
support this feature. Thus, there are several logging files:

* all.log - contains all logs
* thread-main.log - contains logs for the **main** thread
* thread-<class-name>.log - contains logs for the particular class that was run (e.g. "thread-MoveElementUITest.log").
  This logging pattern is applied for those tests, that were run in concurrency mode.

### Test rules

* Each test ends with the word **Test**.
* If a test updates a resource, usually there is present a method or part of code that reverts the changes or removes
  the fixture.
* If a test creates a resource, there is always present a test method or part of code that deletes the resource.

## Allure reports processing

To generate and run the Allure report, execute the maven command:

```
mvn allure:serve
```

It will start the browser on the local host and show the Allure report with different sections.

# API tests flow

The API tests consists of the next stages:

1. Building and sending a request.
2. Response validation using Schema Validator.
3. Post-response validation.

During validation some information is added to the test case of the test step.

## Response validation

### Response validation using Schema Validator

Schema validation — is the validation that is applied for all without exception tests and is implemented in the class
*AssertUtil*. The main assertion method in this class has the *@Step* annotation, that allows to save an assertion as a
step.

### Post-response validation

Post-response validation — is the assertion that is applied to the response to test the scenarios that cannot be tested using Schema Validator.

## Logging && asserting

As it was mentioned in the previous articles, the request and response details are logged into an Allure
report.

### RestAssuredRequestFilter

This class is used to log the request's and response's details and add them to the Allure report.

### AssertUtil

This class is used to perform validations on responses from API.


### AllureLogger

This class is used to change in the runtime the Allure report, especially for failed tests (add details, status code,
etc.).

### AllureListener

This class is used to change the Allure report and log on the test's stage (on start, on failure, etc.).

## Concurrent execution

As it was mentioned in one of the previous sections, the automation framework supports concurrent tests execution, but only
for API tests, the configuration is set for parallel execution on method level with thread count 3.

## Authors

* Ivan Patiuk - *architect and developer*