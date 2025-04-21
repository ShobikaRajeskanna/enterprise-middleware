//package uk.ac.newcastle.enterprisemiddleware.contact;
//
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import uk.ac.newcastle.enterprisemiddleware.model.Booking;
//import uk.ac.newcastle.enterprisemiddleware.model.Customer;
//import uk.ac.newcastle.enterprisemiddleware.model.GuestBooking;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static io.restassured.RestAssured.given;
//
//public class GuestBookingRestServiceIntegration {
//
//    @BeforeAll
//    public static void setup() {
//        // Set the base URI for REST Assured
//        RestAssured.baseURI = "http://localhost:8080"; // Update with the correct port if different
//    }
//
//    @Test
//    public void testCreateGuestBookingSuccess() {
//        // Create a Customer object
//        Customer customer = new Customer();
//        customer.setFirstName("John");
//        customer.setLastName("Doe");
//        customer.setEmail("johndoe@example.com");
//        customer.setPhoneNumber("(123) 456-7890");
//        customer.setState("Active");
//
//        // Create a Booking object
//        Booking booking = new Booking();
//        booking.setStatus("Confirmed");
//
//        // Create a GuestBooking object
//        GuestBooking guestBooking = new GuestBooking();
//        guestBooking.setCustomer(customer);
//        guestBooking.setBooking(booking);
//
//        // Send a POST request with GuestBooking
//        Response response = given()
//                .contentType(ContentType.JSON)
//                .body(guestBooking)
//                .when()
//                .post("/guestbooking")
//                .then()
//                .statusCode(201)
//                .extract()
//                .response();
//
//        // Check the response
//        assertEquals(201, response.getStatusCode());
//
//        // Optionally, parse the response to check the returned Booking data
//        Booking createdBooking = response.getBody().as(Booking.class);
//        assertEquals("Confirmed", createdBooking.getStatus());
//    }
//
//    @Test
//    public void testCreateGuestBookingFailure() {
//        // Create a Customer object without a required field (e.g., no email) to simulate a failure
//        Customer customer = new Customer();
//        customer.setFirstName("Jane");
//        customer.setLastName("Doe");
//        customer.setPhoneNumber("(123) 456-7890");
//
//        // Create a Booking object
//        Booking booking = new Booking();
//        booking.setStatus("Confirmed");
//
//        // Create a GuestBooking object
//        GuestBooking guestBooking = new GuestBooking();
//        guestBooking.setCustomer(customer);
//        guestBooking.setBooking(booking);
//
//        // Send a POST request with incomplete GuestBooking data to trigger an error
//        Response response = given()
//                .contentType(ContentType.JSON)
//                .body(guestBooking)
//                .when()
//                .post("/guestbooking")
//                .then()
//                .statusCode(500)  // Expecting a server error due to validation failure
//                .extract()
//                .response();
//
//        // Check the response status
//        assertEquals(500, response.getStatusCode());
//
//        // Optionally, verify the error message in the response
//        String errorMessage = response.getBody().asString();
//        System.out.println("Error message: " + errorMessage);
//    }
//}
