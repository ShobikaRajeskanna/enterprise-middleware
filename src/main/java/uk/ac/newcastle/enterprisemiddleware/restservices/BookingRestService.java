package uk.ac.newcastle.enterprisemiddleware.restservices;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import uk.ac.newcastle.enterprisemiddleware.model.Booking;
import uk.ac.newcastle.enterprisemiddleware.services.BookingService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService service;

    /**
     * <p>Retrieve all Bookings or filter by customer or hotel if provided.</p>
     */
    @GET
    @Operation(summary = "Fetch all Bookings", description = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBookings(@QueryParam("customerId") Long customerId, @QueryParam("hotelId") Long hotelId) {
        List<Booking> bookings;

        if (customerId != null) {
            bookings = service.findByCustomerId(customerId);
        } else if (hotelId != null) {
            bookings = service.findByHotelId(hotelId);
        } else {
            bookings = service.findAll();
        }

        return Response.ok(bookings).build();
    }

    /**
     * <p>Retrieve a Booking by its ID.</p>
     */
    @GET
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Fetch a Booking by ID", description = "Returns a JSON representation of the Booking object with the provided ID.")
    public Response retrieveBookingById(
            @Parameter(description = "ID of Booking to be fetched", required = true)
            @PathParam("id") Long id) {
        Booking booking = service.findById(id);
        if (booking == null) {
            throw new RestServiceException("No Booking with the ID " + id + " was found!", Response.Status.NOT_FOUND);
        }
        return Response.ok(booking).build();
    }

    /**
     * <p>Create a new Booking from the JSON input.</p>
     */
    @POST
    @Operation(description = "Add a new Booking to the database")
    @Transactional
    public Response createBooking(
            @Parameter(description = "JSON representation of Booking object to be added to the database", required = true)
            Booking booking) {

        if (booking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
        	
            // Go add the new Booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);

        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createBooking completed. Booking = " + booking);
        return builder.build();
    }

    /**
     * <p>Cancel a Booking by its ID.</p>
     */
    /**
     * <p>Delete a Booking by its ID.</p>
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Booking by ID")
    @Transactional
    public Response deleteBooking(
            @Parameter(description = "ID of Booking to be deleted", required = true)
            @PathParam("id") Long id) {

        try {
            // Call the delete method in the service layer using the booking ID
            service.delete(id);

            // Return a no-content response to indicate successful deletion
            return Response.noContent().build();

        } catch (EntityNotFoundException e) {
            throw new RestServiceException("No Booking with the ID " + id + " was found!", Response.Status.NOT_FOUND, e);
        } catch (Exception e) {
            throw new RestServiceException("An error occurred while deleting the booking.", Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }


    /**
     * <p>Updates an existing Booking with the provided ID.</p>
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @Operation(description = "Update a Booking in the database")
    @Transactional
    public Response updateBooking(
            @Parameter(description = "ID of Booking to be updated", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "JSON representation of Booking object to be updated in the database", required = true)
            Booking booking) {

        if (booking == null || booking.getId() == null) {
            throw new RestServiceException("Invalid Booking supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (!id.equals(booking.getId())) {
            // The client attempted to update the read-only ID. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Booking ID in the request body must match that of the Booking being updated");
            throw new RestServiceException("Booking details supplied in request body conflict with another Booking",
                    responseObj, Response.Status.CONFLICT);
        }

        Booking existingBooking = service.findById(id);
        if (existingBooking == null) {
            throw new RestServiceException("No Booking with the ID " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            // Apply changes to the Booking
            service.update(booking);
            builder = Response.ok(booking);

        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("updateBooking completed. Booking = " + booking);
        return builder.build();
    }
}
