package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseClass;
import io.restassured.response.Response;
import model.Booking;
import model.Booking.BookingDates;
import model.BookingWrapper;
import specs.ResponseSpec;
import utils.JSONUtils;

public class BookingAPIUsinFrameworkUsingPOJOBuilderNew extends BaseClass
{

	@Test
	public void createBooking()
	{
		Booking booking=Booking.builder()
		.firstname("Bhagyashree")
		.lastname("API")
		.totalprice(300)
		.depositpaid(true)
		.additionalneeds("lunch")
		.bookingdates(BookingDates.builder().checkin("2024-12-01").checkout("2024-12-15").build())
		.build();
	
		Response resp=sendRequest("Post", "/booking",booking);
		
		resp.then().spec(ResponseSpec.responseSpec());
		
		System.out.println(resp.asPrettyString());
		
		BookingWrapper responseWrapper = JSONUtils.deserialize(resp, BookingWrapper.class);
		
        Booking responseBooking = responseWrapper.getBooking();
		
        System.out.println(responseWrapper.getBookingid());
        
        Assert.assertNotNull(responseWrapper.getBookingid());
        
        Assert.assertNotNull(responseBooking.getFirstname());
        
        System.out.println(responseBooking.getFirstname());
	}
	
}
