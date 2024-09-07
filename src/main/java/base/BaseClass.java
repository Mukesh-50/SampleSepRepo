package base;

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.testng.annotations.BeforeClass;

import config.ConfigManager;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import specs.RequestSpec;

public class BaseClass {
	RequestSpecification requestSpec;

	@BeforeClass
	public void setup() 
	{
		System.out.println("******* Running Setup *********");

		requestSpec = RequestSpec.spec();
		
		 generateAndStoreToken();
	}
	

	// Method to generate a token and update it in the config file using RequestSpec
	private void generateAndStoreToken() 
	{
	    // Check the regenerate flag from the configuration
	    boolean regenerate = Boolean.parseBoolean(ConfigManager.getPropertyFromConfig("regenerate"));

	    // Check if the token is already present in the configuration
	    String currentToken = ConfigManager.getPropertyFromConfig("token");

	    // Determine whether to generate a new token based on the regenerate flag or the token's presence
	    if (regenerate || currentToken == null || currentToken.equals("null") || currentToken.isEmpty()) {
	        System.out.println("Regenerate flag is true or token is null/empty, generating a new token.");

	        // Get the request specification without adding the token for the auth request
	        RequestSpecification authSpec = RequestSpec.spec()
	                .basePath("/auth") // Set the specific path for auth
	                .body("{ \"username\" : \"admin\", \"password\" : \"password123\" }"); // Add auth payload

	        // Send POST request to the /auth endpoint to generate the token
	        Response response = given()
	                .spec(authSpec)
	                .post();

	        // Extract the token from the response
	        String token = response.jsonPath().getString("token");

	        // Update the token value in the configuration file if the token is successfully generated
	        if (token != null) 
	        {
	            ConfigManager.updateTokenInConfig(token);
	            
	            System.out.println("New token generated and updated in config: " + token);
	        } else {
	            throw new RuntimeException("Failed to generate token. Response: " + response.asString());
	        }
	    } else {
	        // If a valid token is already present and regenerate flag is false, skip generating a new token
	        System.out.println("Regenerate flag is false and token is present in config, skipping token generation.");
	    }
	}


	protected Response sendRequest(String method, String resourceName, Object payload, Map<String, Object> headersForAPI,
			Map<String, Object> queryParameterForAPI, String authtoken) {

		RequestSpecification req = given().spec(requestSpec);

		if (headersForAPI != null) {
			req.headers(headersForAPI);
		}

		if (queryParameterForAPI != null) {
			req.queryParams(queryParameterForAPI);
		}

		if (payload != null) {
			req.body(payload);
		}

		if (authtoken != null) {
			req.auth().preemptive().oauth2(authtoken);
		}

		// Get, GET, get
		switch (method.toUpperCase()) {
		case "GET":
			return req.when().get(resourceName);

		case "POST":
			return req.when().post(resourceName);

		case "PUT":
			return req.when().put(resourceName);

		case "PATCH":
			return req.when().patch(resourceName);

		case "DELETE":
			return req.when().delete(resourceName);

		default:
			throw new IllegalArgumentException(
					"Sorry current we dont support " + method + " Please use GET,POST,PUT,PATCH,DELETE ");

		}
		
	
	}
	public Response sendRequest(String method, String resourceName, Object payload)
	{
		return sendRequest(method, resourceName, payload, null, null, null);
	}
	public Response sendRequest(String method, String resourceName)
	{
		return sendRequest(method, resourceName, null, null, null, null);
	}
	
	public Response sendRequest(String method, String resourceName, Object payload,Map<String, Object> headers)
	{
		return sendRequest(method, resourceName, payload, headers, null, null);
	}
	
	public Response sendRequest(String method, String resourceName, Object payload,Map<String, Object> headers,Map<String, Object> query)
	{
		return sendRequest(method, resourceName, payload, headers, query, null);
	}

}
