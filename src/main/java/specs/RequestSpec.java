package specs;

import config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {

    public static RequestSpecification spec() {

        // Read log settings from config
        boolean requestLogs = Boolean.parseBoolean(ConfigManager.getPropertyFromConfig("requestlogs"));
       

        // Set up the RequestSpecBuilder with conditional logging
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getPropertyFromConfig("baseURL"))
                .setContentType(ContentType.JSON);

        // Apply request and response logging based on config
        if (requestLogs) {
            builder.log(LogDetail.ALL);  // Log all request details if requestLogs is true
        }
        
        String token = ConfigManager.getPropertyFromConfig("token");
        
        if (token != null && !token.equals("null")) 
        {
            builder.addHeader("Cookie", "token=" + token); // Add token in Cookie header
        }
        
        // Create RequestSpecification from builder
        RequestSpecification req = builder.build();

        return req;
    }
}
