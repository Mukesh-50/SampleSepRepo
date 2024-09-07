package specs;

import config.ConfigManager;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpec {

    private static final boolean responseLogs = Boolean.parseBoolean(ConfigManager.getPropertyFromConfig("responselogs"));

    public static ResponseSpecification responseSpec() {

        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON);

        // Conditionally log response details based on config
        if (responseLogs) {
            builder.log(LogDetail.ALL);  // Log all response details if responseLogs is true
        }

        return builder.build();
    }

    public static ResponseSpecification responseSpecFor201() {

        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON);

        // Conditionally log response details based on config
        if (responseLogs) {
            builder.log(LogDetail.ALL);  // Log all response details if responseLogs is true
        }

        return builder.build();
    }

    public static ResponseSpecification responseSpecFor404() {

        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .expectStatusCode(404)
                .expectContentType(ContentType.JSON);

        // Conditionally log response details based on config
        if (responseLogs) {
            builder.log(LogDetail.ALL);  // Log all response details if responseLogs is true
        }

        return builder.build();
    }
}
