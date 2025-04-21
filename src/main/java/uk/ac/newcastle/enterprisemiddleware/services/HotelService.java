package uk.ac.newcastle.enterprisemiddleware.services;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

import uk.ac.newcastle.enterprisemiddleware.model.Hotel;
import uk.ac.newcastle.enterprisemiddleware.repository.HotelRepository;
import uk.ac.newcastle.enterprisemiddleware.validator.HotelValidator;

import java.util.List;
import java.util.logging.Logger;

@Dependent
public class HotelService {

    @Inject
    Logger log;

    @Inject
    HotelValidator validator;

    @Inject
    HotelRepository hotelRepository;

    /**
     * Returns a list of all hotels.
     *
     * @return List of Hotel objects
     */
    public List<Hotel> listAllHotels() {
        return hotelRepository.listAll();
    }

    /**
     * Finds a specific Hotel by its ID.
     *
     * @param id The ID of the hotel to be retrieved
     * @return The Hotel with the specified ID, or null if not found
     */
    public Hotel findHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    /**
     * Creates a new Hotel entity after validation.
     *
     * @param hotel The Hotel object to be created
     * @return The created Hotel object
     * @throws ConstraintViolationException if validation fails
     * @throws Exception for any other errors
     */
    public Hotel createHotel(Hotel hotel) throws Exception {
        log.info("HotelService.createHotel() - Creating Hotel: " + hotel.getName());

        // Validate the hotel details
        validator.validateHotel(hotel);

        // Persist the hotel to the database
        return hotelRepository.create(hotel);
    }

    /**
     * Updates an existing Hotel entity after validation.
     *
     * @param hotel The Hotel object with updated information
     * @return The updated Hotel object
     * @throws ConstraintViolationException if validation fails
     * @throws Exception for any other errors
     */
    public Hotel updateHotel(Hotel hotel) throws Exception {
        log.info("HotelService.updateHotel() - Updating Hotel: " + hotel.getName());

        // Validate the hotel details
        validator.validateHotel(hotel);

        // Update the hotel information
        return hotelRepository.update(hotel);
    }

    /**
     * Deletes an existing Hotel entity.
     *
     * @param hotelId The ID of the Hotel to delete
     * @return true if the hotel was deleted successfully, false otherwise
     * @throws Exception if an error occurs during deletion
     */
    public boolean deleteHotel(Long hotelId) throws Exception {
        log.info("HotelService.deleteHotel() - Deleting Hotel with ID: " + hotelId);

        Hotel hotel = hotelRepository.findById(hotelId);
        if (hotel != null) {
            hotelRepository.delete(hotel);
            log.info("HotelService.deleteHotel() - Successfully deleted Hotel and associated bookings with ID: " + hotelId);
            return true;
        } else {
            log.info("HotelService.deleteHotel() - Hotel not found, cannot delete.");
            return false;
        }
    }
}
