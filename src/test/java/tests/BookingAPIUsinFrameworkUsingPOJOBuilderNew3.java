package tests;

import base.BaseClass;
import config.ConfigManager;
import io.restassured.response.Response;
import model.Booking;
import model.Booking.BookingDates;
import org.testng.Assert;
import org.testng.annotations.Test;
import specs.ResponseSpec;
import utils.JSONUtils;

import java.util.HashMap;
import java.util.Map;

public class BookingAPIUsinFrameworkUsingPOJOBuilderNew3 extends BaseClass {

    private int lastBookingId;

    @Test(priority = 1)
    public void createBooking() {
        // Creating a Booking object using Builder pattern
        Booking booking = Booking.builder()
                .firstname("Bhagyashree")
                .lastname("API")
                .totalprice(300)
                .depositpaid(true)
                .additionalneeds("lunch")
                .bookingdates(BookingDates.builder().checkin("2024-12-01").checkout("2024-12-15").build())
                .build();

        // Sending the request
        Response resp = sendRequest("POST", "/booking", booking);

        // Validating response spec
        resp.then().spec(ResponseSpec.responseSpec());

        // Extract booking ID from the response
        lastBookingId=JSONUtils.getInt(resp, "bookingid");

        // Assertions
        Assert.assertNotNull(lastBookingId, "Booking ID should not be null");

        System.out.println("Booking created with ID: " + lastBookingId);
    }

    @Test(priority = 2, dependsOnMethods = "createBooking")
    public void updateBooking() {
        // Creating a new Booking object with updated details
        Booking updatedBooking = Booking.builder()
                .firstname("Mukesh")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .additionalneeds("Breakfast")
                .bookingdates(BookingDates.builder().checkin("2018-01-01").checkout("2019-01-01").build())
                .build();

        // Prepare headers for the update request
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Cookie", "token=" + ConfigManager.getPropertyFromConfig("token"));

        // Sending the PUT request to update the booking using the sendRequest method
        Response response = sendRequest("PUT", "/booking/" + lastBookingId, updatedBooking, headers, null, null);

        System.out.println(response.asPrettyString());

        // Validating response spec
        response.then().spec(ResponseSpec.responseSpec()); // Expecting status code 200

        // Verifying the 'firstname' is updated to "Mukesh"
        String updatedFirstname = JSONUtils.getString(response, "firstname");
        Assert.assertEquals(updatedFirstname, "Mukesh", "Firstname should be updated to Mukesh");

        // Print response for verification
        System.out.println("Updated booking response: " + response.asPrettyString());
    }

    @Test(priority = 3, dependsOnMethods = "updateBooking")
    public void deleteBooking() {
        // Prepare headers for the delete request
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Cookie", "token=" + ConfigManager.getPropertyFromConfig("token"));

        // Sending the DELETE request to delete the booking using the sendRequest method
        Response response = sendRequest("DELETE", "/booking/" + lastBookingId, null, headers, null, null);

        // Print response for verification
        System.out.println("Delete booking response: " + response.asPrettyString());

        // Validating response status code is 201 for successful deletion
        response.then().statusCode(201); // DELETE requests typically return 201 status for successful operations

        // Verify the booking no longer exists (additional optional check)
        Response getResponse = sendRequest("GET", "/booking/" + lastBookingId, null, null, null, null);
        
        Assert.assertEquals(getResponse.statusCode(), 404, "Booking should be deleted and not found.");
    }
}
