package uk.ac.newcastle.enterprisemiddleware.repository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import uk.ac.newcastle.enterprisemiddleware.model.Hotel;

import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class HotelRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * Returns a list of all persisted Hotel objects, sorted by name.
     *
     * @return List of Hotel objects
     */
    public List<Hotel> listAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hotel> criteria = cb.createQuery(Hotel.class);
        Root<Hotel> hotel = criteria.from(Hotel.class);
        criteria.select(hotel).orderBy(cb.asc(hotel.get("name")));
        return em.createQuery(criteria).getResultList();
    }

    /**
     * Finds a Hotel by its ID.
     *
     * @param id The ID of the hotel
     * @return The Hotel object with the specified ID, or null if not found
     */
    public Hotel findById(Long id) {
        return em.find(Hotel.class, id);
    }

    /**
     * Finds a Hotel by its name and location (for uniqueness validation).
     *
     * @param name The name of the hotel
     * @param location The location of the hotel
     * @return The first Hotel object with the specified name and location, or null if not found
     */
    public Hotel findByNameAndLocation(String name, String location) {
        TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h WHERE h.name = :name AND h.location = :location", Hotel.class);
        query.setParameter("name", name);
        query.setParameter("location", location);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Persists the given Hotel object to the database.
     *
     * @param hotel The Hotel object to persist
     * @return The persisted Hotel object
     * @throws Exception
     */
    public Hotel create(Hotel hotel) throws Exception {
        log.info("HotelRepository.create() - Creating Hotel: " + hotel.getName());
        em.persist(hotel);
        return hotel;
    }

    /**
     * Updates an existing Hotel object in the database.
     *
     * @param hotel The Hotel object to update
     * @return The updated Hotel object
     * @throws Exception
     */
    public Hotel update(Hotel hotel) throws Exception {
        log.info("HotelRepository.update() - Updating Hotel: " + hotel.getName());
        em.merge(hotel);
        return hotel;
    }

    /**
     * Deletes a Hotel object from the database if found.
     * Cascade deletion of associated entities like Bookings is handled automatically.
     *
     * @param hotel The Hotel object to delete
     * @return The deleted Hotel object
     * @throws Exception
     */
    public Hotel delete(Hotel hotel) throws Exception {
        log.info("HotelRepository.delete() - Deleting Hotel: " + hotel.getName());
        if (hotel.getId() != null) {
            em.remove(em.merge(hotel)); // Cascade deletes bookings associated with this hotel
        } else {
            log.info("HotelRepository.delete() - No ID was found, cannot delete.");
        }
        return hotel;
    }
}
