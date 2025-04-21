//package uk.ac.newcastle.enterprisemiddleware.contact;
//
//import io.quarkus.test.common.QuarkusTestResource;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.h2.H2DatabaseTestResource;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import uk.ac.newcastle.enterprisemiddleware.model.Customer;
//import uk.ac.newcastle.enterprisemiddleware.restservices.CustomerRestService;
//
//import org.junit.jupiter.api.*;
//
//import java.util.Calendar;
//
//import static io.restassured.RestAssured.given;
//import static io.restassured.RestAssured.when;
////import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
///**
// * Integration tests for the Customer REST service.
// * This test class ensures the functionality of Customer creation, retrieval, update, and deletion.
// * The tests utilize an in-memory H2 database for isolated testing.
// */
//@QuarkusTest
//@TestHTTPEndpoint(CustomerRestService.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@QuarkusTestResource(H2DatabaseTestResource.class)
//class CustomerRestServiceIntegrationTest {
//
//    private static Customer customer;
//
//    /**
//     * Set up a sample customer for testing.
//     */
//    @BeforeAll
//    static void setup() {
//        customer = new Customer();
//        customer.setFirstName("John");
//        customer.setLastName("Doe");
//        customer.setEmail("john.doe@example.com");
//        customer.setPhoneNumber("1234567890");
//        customer.setState("Active");
//        
//        // Set birth date
//        Calendar birthDate = Calendar.getInstance();
//        birthDate.set(1990, Calendar.JANUARY, 1);
//        customer.setBirthDate(birthDate.getTime());
//    }
//
//    /**
//     * Test case for creating a new Customer via POST request.
//     */
//    @Test
//    @Order(1)
//    void testCreateCustomer() {
//        Response response = given()
//            .contentType(ContentType.JSON)
//            .body(customer)
//            .when().post()
//            .then()
//            .extract().response();
//        
//        // Log the server's response for debugging if needed
//        System.out.println("Response Status Code: " + response.getStatusCode());
//        System.out.println("Response Body: " + response.getBody().asString());
//
//        // Check if the customer was created
//        assertEquals(201, response.getStatusCode(), "Expected status code 201 but got " + response.getStatusCode());
//    }
//
//    /**
//     * Test case for retrieving an existing Customer via GET request.
//     */
//    @Test
//    @Order(2)
//    void testRetrieveCustomer() {
//        Response retrieveResponse = given()
//            .when().get("/" + customer.getEmail())
//            .then()
//            .extract().response();
//        
//        // Verify the response code and content
//        assertEquals(200, retrieveResponse.getStatusCode(), "Expected status code 200 but got " + retrieveResponse.getStatusCode());
//        assertTrue(retrieveResponse.getBody().asString().contains("john.doe@example.com"), "Expected to find email in response.");
//    }
//
//    /**
//     * Test case for updating an existing Customer via PUT request.
//     */
//    @Test
//    @Order(3)
//    void testUpdateCustomer() {
//        // Update the phone number
//        customer.setPhoneNumber("0987654321");
//
//        Response updateResponse = given()
//            .contentType(ContentType.JSON)
//            .body(customer)
//            .when().put("/" + customer.getEmail())
//            .then()
//            .extract().response();
//
//        // Verify the response code and updated content
//        assertEquals(200, updateResponse.getStatusCode(), "Expected status code 200 but got " + updateResponse.getStatusCode());
//        assertTrue(updateResponse.getBody().asString().contains("0987654321"), "Expected updated phone number in response.");
//    }
//
//    /**
//     * Test case for deleting an existing Customer via DELETE request.
//     */
//    @Test
//    @Order(4)
//    void testDeleteCustomer() {
//        // Delete the customer
//        Response deleteResponse = when().delete("/" + customer.getEmail())
//            .then()
//            .extract().response();
//
//        assertEquals(204, deleteResponse.getStatusCode(), "Expected status code 204 but got " + deleteResponse.getStatusCode());
//
//        // Verify deletion by attempting to retrieve the customer again
//        Response verifyDeleteResponse = when().get("/" + customer.getEmail())
//            .then()
//            .extract().response();
//
//        assertEquals(404, verifyDeleteResponse.getStatusCode(), "Expected status code 404 after deletion but got " + verifyDeleteResponse.getStatusCode());
//    }
//}
