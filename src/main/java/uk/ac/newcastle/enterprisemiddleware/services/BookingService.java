package uk.ac.newcastle.enterprisemiddleware.services;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import uk.ac.newcastle.enterprisemiddleware.model.Booking;
import uk.ac.newcastle.enterprisemiddleware.repository.BookingRepository;
import uk.ac.newcastle.enterprisemiddleware.validator.BookingValidator;

import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service class controls the core logic for the Booking entity. It includes validation, persistence,
 * and handles any business rules required for managing bookings.</p>
 *
 * <p>It assumes responsibility as the Control layer in the ECB pattern, ensuring consistent rules are applied before
 * interacting with the database.</p>
 *
 * @see BookingValidator
 * @see BookingRepository
 */
@Dependent
public class BookingService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingValidator validator;

    @Inject
    BookingRepository crud;

    /**
     * <p>Returns a list of all persisted {@link Booking} objects.</p>
     *
     * @return List of Booking objects
     */
    public List<Booking> findAll() {
        return crud.findAll();
    }

    /**
     * <p>Returns a Booking by its unique ID.</p>
     *
     * @param id The ID of the booking
     * @return The Booking with the specified ID
     */
    public Booking findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns all bookings associated with a specific customer.</p>
     *
     * @param customerId The customer ID
     * @return List of Booking objects for the specified customer
     */
    public List<Booking> findByCustomerId(Long customerId) {
        return crud.findByCustomerId(customerId);
    }

    /**
     * <p>Returns all bookings associated with a specific hotel.</p>
     *
     * @param hotelId The hotel ID
     * @return List of Booking objects for the specified hotel
     */
    public List<Booking> findByHotelId(Long hotelId) {
        return crud.findByHotelId(hotelId);
    }

    /**
     * <p>Creates a new Booking in the database after validation.</p>
     *
     * @param booking The Booking object to be created
     * @return The persisted Booking object
     * @throws Exception If there are validation or persistence issues
     */
    public Booking create(Booking booking) throws Exception {
        log.info("BookingService.create() - Creating booking for customer: " + booking.getCustomer().getId() +
                 ", hotel: " + booking.getHotel().getId() + ", on date: " + booking.getBookingDate());

        // Validate the Booking object using BookingValidator
        validator.validateBooking(booking);

        // Persist the Booking
        return crud.create(booking);
    }

    /**
     * <p>Updates an existing Booking in the database after validation.</p>
     *
     * @param booking The Booking object with updated data
     * @return The updated Booking object
     * @throws Exception If there are validation or persistence issues
     */
    public Booking update(Booking booking) throws Exception {
        log.info("BookingService.update() - Updating booking for customer: " + booking.getCustomer().getId() +
                 ", hotel: " + booking.getHotel().getId());

        // Validate the updated Booking object
        validator.validateBooking(booking);

        // Update the Booking
        return crud.update(booking);
    }

    /**
     * <p>Deletes an existing Booking by its ID from the database.</p>
     *
     * @param id The ID of the Booking to delete
     * @throws Exception If there are issues during deletion
     */
    public void delete(Long id) throws Exception {
        Booking booking = crud.findById(id);
        if (booking == null) {
            throw new EntityNotFoundException("Booking not found with ID: " + id);
        }
        log.info("BookingService.delete() - Deleting booking with ID: " + id);
        crud.delete(booking);
    }

}
