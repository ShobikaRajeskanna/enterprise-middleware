package uk.ac.newcastle.enterprisemiddleware.repository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;

import uk.ac.newcastle.enterprisemiddleware.model.Customer;

import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link CustomerService}) with the
 * Domain/Entity Object (see {@link Customer}).</p>
 *
 * <p>Methods are 'package' scope, meaning they should only be accessed by a Service/Control object.</p>
 * 
 * @see Customer
 * @see javax.persistence.EntityManager
 */
@RequestScoped
public class CustomerRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Customer objects
     */
    public List<Customer> findAllOrderedByName() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Customer object, specified by a Long id.</p>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    /**
     * <p>Returns a single Customer object, specified by a String email.</p>
     *
     * @param email The email field of the Customer to be returned
     * @return The Customer with the specified email
     */
    public Customer findByEmail(String email) {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class)
                .setParameter("email", email);
        return query.getSingleResult();
    }

    /**
     * <p>Returns a list of Customer objects filtered by a String firstName.</p>
     *
     * @param firstName The firstName field of the Customers to be returned
     * @return The Customers with the specified firstName
     */
    public List<Customer> findAllByFirstName(String firstName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        criteria.select(customer).where(cb.equal(customer.get("firstName"), firstName));
        return em.createQuery(criteria).getResultList();
    }

    /**
     * <p>Returns a list of Customer objects filtered by a String lastName.</p>
     *
     * @param lastName The lastName field of the Customers to be returned
     * @return The Customers with the specified lastName
     */
    public List<Customer> findAllByLastName(String lastName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        criteria.select(customer).where(cb.equal(customer.get("lastName"), lastName));
        return em.createQuery(criteria).getResultList();
    }

    /**
     * <p>Persists the provided Customer object to the database using the EntityManager.</p>
     *
     * @param customer The Customer object to be persisted
     * @return The persisted Customer object
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Customer create(Customer customer) throws Exception {
        log.info("CustomerRepository.create() - Creating " + customer.getFirstName() + " " + customer.getLastName());

        em.persist(customer);
        return customer;
    }

    /**
     * <p>Updates an existing Customer object in the database with the provided Customer object.</p>
     *
     * @param customer The Customer object to be updated
     * @return The updated Customer object
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Customer update(Customer customer) throws Exception {
        log.info("CustomerRepository.update() - Updating " + customer.getFirstName() + " " + customer.getLastName());

        em.merge(customer);
        return customer;
    }

    /**
     * <p>Deletes the provided Customer object from the database if found.</p>
     *
     * @param customer The Customer object to be deleted
     * @return The deleted Customer object; or null if not found
     * @throws Exception
     */
    public Customer delete(Customer customer) throws Exception {
        log.info("CustomerRepository.delete() - Deleting " + customer.getFirstName() + " " + customer.getLastName());

        if (customer.getId() != null) {
            em.remove(em.merge(customer));
        } else {
            log.info("CustomerRepository.delete() - No ID was found, so deletion is not possible.");
        }

        return customer;
    }

}
