package uk.ac.newcastle.enterprisemiddleware.restservices;

import uk.ac.newcastle.enterprisemiddleware.model.GuestBooking;
import uk.ac.newcastle.enterprisemiddleware.model.Booking;
import uk.ac.newcastle.enterprisemiddleware.model.Customer;
import uk.ac.newcastle.enterprisemiddleware.services.BookingService;
import uk.ac.newcastle.enterprisemiddleware.services.CustomerService;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/guestbooking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {

    @Inject
    CustomerService customerService;

    @Inject
    BookingService bookingService;

    @Inject
    UserTransaction userTransaction;

    @POST
    public Response createGuestBooking(GuestBooking guestBooking) {
        try {
            // Start the transaction manually
            userTransaction.begin();

            // Step 1: Persist the Customer
            Customer customer = guestBooking.getCustomer();
            customerService.create(customer);  // Assuming CustomerService has a create method

            // Step 2: Set the newly created Customer on the Booking and persist the Booking
            Booking booking = guestBooking.getBooking();
            booking.setCustomer(customer);
            bookingService.create(booking);  // Assuming BookingService has a create method

            // Commit the transaction
            userTransaction.commit();

            // Return response with Booking and status 201 Created
            return Response.status(Response.Status.CREATED).entity(booking).build();

        } catch (Exception e) {
            try {
                // Rollback if any error occurs
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                // Handle rollback failure
                e.addSuppressed(rollbackEx);
            }
            // Return error response with status 500
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error creating GuestBooking: " + e.getMessage())
                           .build();
        }
    }
}
