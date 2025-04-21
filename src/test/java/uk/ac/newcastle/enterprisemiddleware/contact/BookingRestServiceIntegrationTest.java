//package uk.ac.newcastle.enterprisemiddleware.contact;
//
//import io.quarkus.test.common.QuarkusTestResource;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.h2.H2DatabaseTestResource;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import uk.ac.newcastle.enterprisemiddleware.model.Booking;
//import uk.ac.newcastle.enterprisemiddleware.model.Customer;
//import uk.ac.newcastle.enterprisemiddleware.model.Hotel;
//import uk.ac.newcastle.enterprisemiddleware.restservices.BookingRestService;
//
//import org.junit.jupiter.api.*;
//
//import java.time.LocalDate;
//
//import static io.restassured.RestAssured.given;
//import static io.restassured.RestAssured.when;
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@QuarkusTest
//@TestHTTPEndpoint(BookingRestService.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@QuarkusTestResource(H2DatabaseTestResource.class)
//class BookingRestServiceIntegrationTest {
//
//    private static Booking booking;
//    private static Customer customer;
//    private static Hotel hotel;
//
//    @BeforeAll
//    static void setup() {
//        // Setup a customer object
//        customer = new Customer();
//        customer.setFirstName("TestCustomer");
//        customer.setLastName("Account");
//        customer.setEmail("testcustomer@email.com");
//
//        // Setup a hotel object
//        hotel = new Hotel();
//        hotel.setName("TestHotel");
//        hotel.setLocation("TestCity");
//
//        // Initialize booking with customer and hotel
//        booking = new Booking();
//        booking.setCustomer(customer);
//        booking.setHotel(hotel);
//        booking.setBookingDate(LocalDate.now());
//        booking.setStatus("Confirmed");
//    }
//
//    @Test
//    @Order(1)
//    public void testCanCreateBooking() {
//        // Create the customer and hotel first if they do not exist
//        createCustomerIfNotExists(customer);
//        createHotelIfNotExists(hotel);
//
//        given().
//                contentType(ContentType.JSON).
//                body(booking).
//        when().
//                post().
//        then().
//                statusCode(201);
//    }
//
//    @Test
//    @Order(2)
//    public void testCanGetBookings() {
//        Response response = when().
//                get().
//        then().
//                statusCode(200).
//                extract().response();
//
//        Booking[] result = response.body().as(Booking[].class);
//
//        System.out.println(result[0]);
//
//        assertEquals(1, result.length);
//        assertTrue(booking.getCustomer().getFirstName().equals(result[0].getCustomer().getFirstName()), "Customer first name not equal");
//        assertTrue(booking.getHotel().getName().equals(result[0].getHotel().getName()), "Hotel name not equal");
//        assertTrue(booking.getStatus().equals(result[0].getStatus()), "Status not equal");
//    }
//
//    @Test
//    @Order(3)
//    public void testDuplicateBookingCausesError() {
//        given().
//                contentType(ContentType.JSON).
//                body(booking).
//        when().
//                post().
//        then().
//                statusCode(409).
//                body("reasons", containsString("already booked this hotel on the specified date"));
//    }
//
//    @Test
//    @Order(4)
//    public void testCanDeleteBooking() {
//        Response response = when().
//                get().
//        then().
//                statusCode(200).
//                extract().response();
//
//        Booking[] result = response.body().as(Booking[].class);
//
//        when().
//                delete(result[0].getId().toString()).
//        then().
//                statusCode(204);
//    }
//
//    /**
//     * Helper method to create a customer if not exists
//     */
//    private void createCustomerIfNotExists(Customer customer) {
//        Response response = given().
//                contentType(ContentType.JSON).
//                body(customer).
//        when().
//                post("/api/customers").
//        then().
//                extract().response();
//        if (response.statusCode() == 201) {
//            customer.setId(response.body().as(Customer.class).getId());
//        }
//    }
//
//    /**
//     * Helper method to create a hotel if not exists
//     */
//    private void createHotelIfNotExists(Hotel hotel) {
//        Response response = given().
//                contentType(ContentType.JSON).
//                body(hotel).
//        when().
//                post("/api/hotels").
//        then().
//                extract().response();
//        if (response.statusCode() == 201) {
//            hotel.setId(response.body().as(Hotel.class).getId());
//        }
//    }
//}
