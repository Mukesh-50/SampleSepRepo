package tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseClass;
import config.ConfigManager;
import io.restassured.response.Response;
import model.Booking;
import model.Booking.BookingDates;
import model.BookingWrapper;
import specs.ResponseSpec;
import utils.JSONUtils;

public class BookingAPIUsinFrameworkUsingPOJOBuilderNew4 extends BaseClass {

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
       // lastBookingId = resp.jsonPath().getInt("bookingid");
        
        lastBookingId=JSONUtils.getInt(resp, "bookingid");

        // Deserialize the booking part of the response to validate the created booking
        BookingWrapper bookingID = JSONUtils.deserialize(resp, BookingWrapper.class);
        
        Booking createdBooking=bookingID.getBooking();

        // Assertions
        Assert.assertNotNull(bookingID.getBooking(), "Booking ID should not be null");
        Assert.assertEquals(createdBooking.getFirstname(), booking.getFirstname(), "Firstname should match");
        Assert.assertEquals(createdBooking.getLastname(), booking.getLastname(), "Lastname should match");
        Assert.assertEquals(createdBooking.getTotalprice(), booking.getTotalprice(), "Total price should match");
        Assert.assertEquals(createdBooking.isDepositpaid(), booking.isDepositpaid(), "Deposit paid status should match");
        Assert.assertEquals(createdBooking.getAdditionalneeds(), booking.getAdditionalneeds(), "Additional needs should match");

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

        // Validating response spec
        response.then().spec(ResponseSpec.responseSpec()); // Expecting status code 200

        // Deserialize the response to validate the updated booking
        Booking responseBooking = JSONUtils.deserialize(response, Booking.class);

        // Verifying the 'firstname' is updated to "Mukesh"
        Assert.assertEquals(responseBooking.getFirstname(), "Mukesh", "Firstname should be updated to Mukesh");

        // Validate other fields
        Assert.assertEquals(responseBooking.getLastname(), updatedBooking.getLastname(), "Lastname should match");
        Assert.assertEquals(responseBooking.getTotalprice(), updatedBooking.getTotalprice(), "Total price should match");
        Assert.assertEquals(responseBooking.isDepositpaid(), updatedBooking.isDepositpaid(), "Deposit paid status should match");
        Assert.assertEquals(responseBooking.getAdditionalneeds(), updatedBooking.getAdditionalneeds(), "Additional needs should match");

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
        Response getResponse = sendRequest("GET", "/booking/" + lastBookingId);
        
        Assert.assertEquals(getResponse.statusCode(), 404, "Booking should be deleted and not found.");
    }
}
