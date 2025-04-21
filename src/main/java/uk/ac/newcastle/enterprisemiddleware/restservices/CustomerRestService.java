package uk.ac.newcastle.enterprisemiddleware.restservices;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import uk.ac.newcastle.enterprisemiddleware.model.Customer;
import uk.ac.newcastle.enterprisemiddleware.services.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST API for managing customers, including creating, retrieving, updating, and deleting customers.
 * Supports cascading deletion of bookings associated with customers.
 */
@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    Logger log;

    @Inject
    CustomerService service;

    /**
     * Retrieves all customers, with optional filtering by first name and last name.
     * @param firstname The first name to filter by.
     * @param lastname The last name to filter by.
     * @return A JSON array of Customer objects.
     */
    @GET
    @Operation(summary = "Fetch all Customers", description = "Returns a JSON array of all stored Customer objects.")
    public Response retrieveAllCustomers(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
        List<Customer> customers;

        if (firstname == null && lastname == null) {
            customers = service.findAllOrderedByName();
        } else if (lastname == null) {
            customers = service.findAllByFirstName(firstname);
        } else if (firstname == null) {
            customers = service.findAllByLastName(lastname);
        } else {
            customers = service.findAllByFirstName(firstname);
            customers.retainAll(service.findAllByLastName(lastname));
        }
        return Response.ok(customers).build();
    }

    /**
     * Retrieves a specific customer by their ID.
     * @param id The ID of the customer to retrieve.
     * @return The customer with the specified ID, or 404 error if not found.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieve a customer", description = "Retrieves a specific customer by ID.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "The customer"),
            @APIResponse(responseCode = "404", description = "Customer not found")
    })
    public Response getCustomer(
            @Parameter(description = "ID of the customer to retrieve", required = true)
            @PathParam("id") Long id) {
        Customer customer = service.findById(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(customer).build();
    }

    /**
     * Creates a new customer in the system.
     * @param customer The customer to create.
     * @return A 201 response with the created customer, or a 400 error if validation fails.
     */
    @POST
    @Operation(summary = "Create a new customer", description = "Adds a new customer to the list.")
    @APIResponse(responseCode = "201", description = "The created customer")
    @Transactional
    public Response createCustomer(Customer customer) {
        try {
            Customer createdCustomer = service.create(customer);
            return Response.status(Response.Status.CREATED).entity(createdCustomer).build();
        } catch (ConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred").build();
        }
    }

    /**
     * Updates an existing customer based on their ID.
     * @param id The ID of the customer to update.
     * @param customer The updated customer details.
     * @return The updated customer, or a 404 error if the customer is not found.
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a customer", description = "Updates details of an existing customer.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "The updated customer"),
            @APIResponse(responseCode = "404", description = "Customer not found")
    })
    @Transactional
    public Response updateCustomer(
            @Parameter(description = "ID of the customer to update", required = true)
            @PathParam("id") Long id, Customer customer) {
        try {
            Customer existingCustomer = service.findById(id);
            if (existingCustomer == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            customer.setId(id);  // Ensure the ID remains the same
            Customer updatedCustomer = service.update(customer);
            return Response.ok(updatedCustomer).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred").build();
        }
    }

    /**
     * Deletes a customer by ID, including associated bookings.
     * @param id The ID of the customer to delete.
     * @return A 204 response if deletion is successful, or a 404 if customer not found.
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete a Customer and associated bookings")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Customer and associated bookings deleted successfully."),
            @APIResponse(responseCode = "404", description = "Customer not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    @Transactional
    public Response deleteCustomer(@PathParam("id") long id) {
        Customer customer = service.findById(id);
        if (customer == null) {
            throw new RestServiceException("No Customer with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        try {
          service.delete(customer); // This will handle cascading deletion of bookings
        } catch (Exception e) {
          log.severe("Failed to delete Customer: " + e.getMessage());
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                  .entity("Failed to delete customer due to internal error").build();
        }        return Response.noContent().build();
    }
}




