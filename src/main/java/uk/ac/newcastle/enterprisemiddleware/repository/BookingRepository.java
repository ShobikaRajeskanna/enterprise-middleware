package uk.ac.newcastle.enterprisemiddleware.repository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import uk.ac.newcastle.enterprisemiddleware.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService}) with the
 * Domain/Entity Object (see {@link Booking}).</p>
 *
 * <p>The BookingRepository provides methods to persist, update, delete, and query Booking entities.</p>
 *
 * @see Booking
 * @see javax.persistence.EntityManager
 */
@RequestScoped
public class BookingRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a list of all persisted {@link Booking} objects.</p>
     *
     * @return List of Booking objects
     */
    public List<Booking> findAll() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Booking object specified by a Long id.</p>
     *
     * @param id The id of the Booking to be returned
     * @return The Booking with the specified id
     */
    public Booking findById(Long id) {
        return em.find(Booking.class, id);
    }

    /**
     * <p>Returns all Booking objects associated with a specific customer.</p>
     *
     * @param customerId The id of the customer
     * @return List of Booking objects associated with the customer
     */
    public List<Booking> findByCustomerId(Long customerId) {
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.customer.id = :customerId", Booking.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    /**
     * <p>Returns all Booking objects associated with a specific hotel.</p>
     *
     * @param hotelId The id of the hotel
     * @return List of Booking objects associated with the hotel
     */
    public List<Booking> findByHotelId(Long hotelId) {
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.hotel.id = :hotelId", Booking.class);
        query.setParameter("hotelId", hotelId);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Booking object specified by customer, hotel, and booking date.</p>
     *
     * @param customerId The id of the customer
     * @param hotelId The id of the hotel
     * @param bookingDate The date of the booking
     * @return The Booking object with the specified customer, hotel, and booking date
     */
    public Booking findByCustomerAndHotelAndDate(Long customerId, Long hotelId, LocalDate bookingDate) {
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.customer.id = :customerId AND b.hotel.id = :hotelId AND b.bookingDate = :bookingDate", Booking.class);
        query.setParameter("customerId", customerId);
        query.setParameter("hotelId", hotelId);
        query.setParameter("bookingDate", bookingDate);
        return query.getSingleResult();
    }

    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws Exception If there are issues during persistence
     */
    public Booking create(Booking booking) throws Exception {
        log.info("BookingRepository.create() - Creating booking for customer: " + booking.getCustomer().getId() +
                 ", hotel: " + booking.getHotel().getId() + ", on date: " + booking.getBookingDate());

        em.persist(booking);

        return booking;
    }

    /**
     * <p>Updates an existing Booking object in the application database.</p>
     *
     * @param booking The Booking object with updated information
     * @return The updated Booking object
     * @throws Exception If there are issues during the update
     */
    public Booking update(Booking booking) throws Exception {
        log.info("BookingRepository.update() - Updating booking with ID: " + booking.getId());

        em.merge(booking);

        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.</p>
     *
     * @param booking The Booking object to be removed
     * @return The deleted Booking object or null if not found
     * @throws Exception If there are issues during deletion
     */
    public Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting booking with ID: " + booking.getId());

        if (booking.getId() != null) {
            em.remove(em.merge(booking));
        } else {
            log.info("BookingRepository.delete() - No ID was found so cannot delete.");
        }

        return booking;
    }
}
