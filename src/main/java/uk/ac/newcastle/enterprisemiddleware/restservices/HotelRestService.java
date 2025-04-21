package uk.ac.newcastle.enterprisemiddleware.restservices;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import uk.ac.newcastle.enterprisemiddleware.model.Hotel;
import uk.ac.newcastle.enterprisemiddleware.services.HotelService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST API for managing hotels, including creating, reading, updating, and deleting hotels.
 * Supports cascading deletion of bookings associated with hotels.
 */
@Path("/hotels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HotelRestService {

    // Injects the HotelService to access business logic for hotel operations
    @Inject
    HotelService hotelService;

    /**
     * Lists all hotels in the system.
     * @return List of all hotels.
     */
    @GET
    @Operation(summary = "List all hotels", description = "Retrieves a list of all hotels.")
    public List<Hotel> listHotels() {
        return hotelService.listAllHotels();
    }

    /**
     * Retrieves a specific hotel by its ID.
     * @param id The ID of the hotel to retrieve.
     * @return The hotel with the specified ID or a 404 error if not found.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Retrieve a hotel", description = "Retrieves a specific hotel by ID.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "The hotel"),
        @APIResponse(responseCode = "404", description = "Hotel not found")
    })
    public Response getHotel(
        @Parameter(description = "ID of the hotel to retrieve", required = true)
        @PathParam("id") Long id) {
        Hotel hotel = hotelService.findHotelById(id);
        if (hotel == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(hotel).build();
    }

    /**
     * Creates a new hotel in the system.
     * @param hotel The hotel to create.
     * @return A 201 response with the created hotel or a 400 error if validation fails.
     */
    @POST
    @Operation(summary = "Create a new hotel", description = "Adds a new hotel to the list.")
    @APIResponse(responseCode = "201", description = "The created hotel")
    @Transactional
    public Response createHotel(Hotel hotel) {
        try {
            Hotel createdHotel = hotelService.createHotel(hotel);
            return Response.status(Response.Status.CREATED).entity(createdHotel).build();
        } catch (ConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred").build();
        }
    }

    /**
     * Updates an existing hotel based on its ID.
     * @param id The ID of the hotel to update.
     * @param hotel The updated hotel details.
     * @return The updated hotel or a 404 error if the hotel is not found.
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a hotel", description = "Updates details of an existing hotel.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "The updated hotel"),
        @APIResponse(responseCode = "404", description = "Hotel not found")
    })
    @Transactional
    public Response updateHotel(
        @Parameter(description = "ID of the hotel to update", required = true)
        @PathParam("id") Long id, Hotel hotel) {
        try {
            Hotel existingHotel = hotelService.findHotelById(id);
            if (existingHotel == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            // Set ID to ensure the hotel is updated rather than a new one created
            hotel.setId(id);  
            Hotel updatedHotel = hotelService.updateHotel(hotel);
            return Response.ok(updatedHotel).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred").build();
        }
    }

    /**
     * Deletes an existing hotel by its ID, including any associated bookings.
     * @param id The ID of the hotel to delete.
     * @return A 204 response if the hotel is deleted or a 404 error if the hotel is not found.
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a hotel", description = "Deletes an existing hotel and its associated bookings.")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Hotel deleted"),
        @APIResponse(responseCode = "404", description = "Hotel not found")
    })
    @Transactional
    public Response deleteHotel(
        @Parameter(description = "ID of the hotel to delete", required = true)
        @PathParam("id") Long id) {
        try {
            Hotel existingHotel = hotelService.findHotelById(id);
            if (existingHotel == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            // Deletes the hotel and cascades to associated bookings
            hotelService.deleteHotel(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred").build();
        }
    }
}
