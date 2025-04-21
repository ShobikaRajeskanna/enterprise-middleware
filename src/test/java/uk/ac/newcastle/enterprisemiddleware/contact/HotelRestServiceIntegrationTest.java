//package uk.ac.newcastle.enterprisemiddleware.contact;
//
////import java.util.Calendar;
//
//import static io.restassured.RestAssured.given;
//import static io.restassured.RestAssured.when;
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//
//import io.quarkus.test.common.QuarkusTestResource;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.h2.H2DatabaseTestResource;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import uk.ac.newcastle.enterprisemiddleware.model.Hotel;
//import uk.ac.newcastle.enterprisemiddleware.restservices.HotelRestService;
//
//@QuarkusTest
//@TestHTTPEndpoint(HotelRestService.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@QuarkusTestResource(H2DatabaseTestResource.class)
//class HotelRestServiceIntegrationTest {
//
//    private static Hotel hotel;
//
//    @BeforeAll
//    static void setup() {
//        hotel = new Hotel();
//        hotel.setName("Sample Hotel");
//        hotel.setLocation("New York");
//        hotel.setDescription("A sample hotel for testing");
//        hotel.setTotalRooms(100);
//        hotel.setAvailableRooms(80);
//    }
//
//    @Test
//    @Order(1)
//    public void testCanCreateHotel() {
//        given().
//                contentType(ContentType.JSON).
//                body(hotel).
//        when().
//                post().
//        then().
//                statusCode(201);
//    }
//
//    @Test
//    @Order(2)
//    public void testCanGetHotels() {
//        Response response = when().
//                get().
//        then().
//                statusCode(200).
//                extract().response();
//
//        Hotel[] result = response.body().as(Hotel[].class);
//
//        System.out.println(result[0]);
//
//        assertEquals(1, result.length);
//        assertTrue(hotel.getName().equals(result[0].getName()), "Hotel name not equal");
//        assertTrue(hotel.getLocation().equals(result[0].getLocation()), "Location not equal");
//        assertTrue(hotel.getDescription().equals(result[0].getDescription()), "Description not equal");
//        assertEquals(hotel.getTotalRooms(), result[0].getTotalRooms(), "Total rooms not equal");
//        assertEquals(hotel.getAvailableRooms(), result[0].getAvailableRooms(), "Available rooms not equal");
//    }
//
//    @Test
//    @Order(3)
//    public void testDuplicateHotelCausesError() {
//        given().
//                contentType(ContentType.JSON).
//                body(hotel).
//        when().
//                post().
//        then().
//                statusCode(409).
//                body("reasons", containsString("already exists"));
//    }
//
//    @Test
//    @Order(4)
//    public void testCanDeleteHotel() {
//        Response response = when().
//                get().
//                then().
//                statusCode(200).
//                extract().response();
//
//        Hotel[] result = response.body().as(Hotel[].class);
//
//        when().
//                delete(result[0].getId().toString()).
//        then().
//                statusCode(204);
//    }
//}
