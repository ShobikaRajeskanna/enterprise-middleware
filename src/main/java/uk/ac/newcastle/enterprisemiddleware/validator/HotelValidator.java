package uk.ac.newcastle.enterprisemiddleware.validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import uk.ac.newcastle.enterprisemiddleware.model.Hotel;
import uk.ac.newcastle.enterprisemiddleware.repository.HotelRepository;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class HotelValidator {

    @Inject
    Validator validator;

    @Inject
    HotelRepository hotelRepository;

    /**
     * Validates the given Hotel object and throws validation exceptions based on the type of error. 
     * If there are standard bean validation errors, it throws a ConstraintViolationException with the set of constraints violated.
     * If a hotel with the same name and location already exists, it throws a ValidationException.
     *
     * @param hotel The Hotel object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If a hotel with the same name and location already exists
     */
    public void validateHotel(Hotel hotel) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for violations.
        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the hotel name and location
        if (hotelAlreadyExists(hotel.getName(), hotel.getLocation(), hotel.getId())) {
            throw new ValidationException("A hotel with the same name and location already exists.");
        }
    }

    /**
     * Checks if a hotel with the same name and location is already registered. This helps simulate a unique constraint
     * for the combination of name and location.
     *
     * @param name The hotel name to check for uniqueness
     * @param location The hotel location to check for uniqueness
     * @param id The hotel id to check against (useful in update scenarios to ignore the current record)
     * @return boolean indicating whether a hotel with the same name and location already exists
     */
    boolean hotelAlreadyExists(String name, String location, Long id) {
        Hotel existingHotel = null;
        Hotel hotelWithID = null;

        try {
            existingHotel = hotelRepository.findByNameAndLocation(name, location);
        } catch (NoResultException e) {
            // Hotel not found, which is expected if no duplicates exist
        }

        if (existingHotel != null && id != null) {
            try {
                hotelWithID = hotelRepository.findById(id);
                if (hotelWithID != null && hotelWithID.getName().equals(name) && hotelWithID.getLocation().equals(location)) {
                    existingHotel = null;
                }
            } catch (NoResultException e) {
                // Hotel with ID not found, which is acceptable for update checks
            }
        }
        return existingHotel != null;
    }
}
