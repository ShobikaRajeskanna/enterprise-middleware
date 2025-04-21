package uk.ac.newcastle.enterprisemiddleware.validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import uk.ac.newcastle.enterprisemiddleware.model.Booking;
import uk.ac.newcastle.enterprisemiddleware.repository.BookingRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements and business rules.</p>
 *
 * <p>Ensures the Booking entity meets the necessary constraints before it is persisted in the database.</p>
 *
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
@ApplicationScoped
public class BookingValidator {

    @Inject
    Validator validator;

    @Inject
    BookingRepository crud;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error.</p>
     *
     * <p>If standard bean validation errors exist, it will throw a ConstraintViolationException with the set of
     * constraints violated.</p>
     *
     * <p>If the error is caused because a booking with the same customer, hotel, and booking date already exists,
     * it throws a regular ValidationException to distinguish it as a business rule violation.</p>
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If a duplicate booking (same customer, hotel, date) exists
     */
    public void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Perform bean validation
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check for duplicate booking
        if (duplicateBookingExists(booking)) {
            throw new ValidationException("Duplicate Booking Violation: Customer has already booked this hotel on the specified date.");
        }
    }

    /**
     * <p>Checks if a booking with the same customer, hotel, and booking date already exists. This helps enforce
     * a unique constraint on these three fields as a business rule.</p>
     *
     * <p>If updating an existing booking, it ignores the current booking's ID to prevent self-conflict.</p>
     *
     * @param booking The booking to check for duplication
     * @return boolean indicating if a duplicate booking exists
     */
    boolean duplicateBookingExists(Booking booking) {
        try {
            Booking existingBooking = crud.findByCustomerAndHotelAndDate(
                    booking.getCustomer().getId(),
                    booking.getHotel().getId(),
                    booking.getBookingDate()
            );

            // If a booking is found with the same details but a different ID, it's a duplicate
            return existingBooking != null && !existingBooking.getId().equals(booking.getId());
        } catch (NoResultException e) {
            // No existing booking found, so it's not a duplicate
            return false;
        }
    }
}
