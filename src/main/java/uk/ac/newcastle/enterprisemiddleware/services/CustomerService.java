package uk.ac.newcastle.enterprisemiddleware.services;

import uk.ac.newcastle.enterprisemiddleware.model.Customer;
import uk.ac.newcastle.enterprisemiddleware.repository.CustomerRepository;
import uk.ac.newcastle.enterprisemiddleware.validator.CustomerValidator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service class performs business logic for Customer operations, including validation and deletion.</p>
 */
@Dependent
public class CustomerService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerValidator validator;

    @Inject
    CustomerRepository crud;

    /**
     * Returns a list of all customers, sorted alphabetically by last name.
     * @return List of all Customer objects.
     */
    public List<Customer> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * Retrieves a customer by ID.
     * @param id The ID of the customer to retrieve.
     * @return The Customer with the specified ID.
     */
    public Customer findById(Long id) {
        return crud.findById(id);
    }

    /**
     * Retrieves a customer by email.
     * @param email The email of the customer to retrieve.
     * @return The Customer with the specified email.
     */
    public Customer findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * Retrieves all customers with a given first name.
     * @param firstName The first name to filter by.
     * @return List of Customers with the specified first name.
     */
    public List<Customer> findAllByFirstName(String firstName) {
        return crud.findAllByFirstName(firstName);
    }
    
    public List<Customer> findAllByLastName(String lastName) {
        return crud.findAllByLastName(lastName);
    }

    /**
     * Creates a new customer and validates data using {@link CustomerValidator}.
     * @param customer The Customer object to create.
     * @return The created Customer object.
     * @throws Exception If a validation error occurs or the area code is invalid.
     */
    public Customer create(Customer customer) throws Exception {
        log.info("CustomerService.create() - Creating " + customer.getFirstName() + " " + customer.getLastName());
        validator.validateCustomer(customer);

        // Additional business logic, if needed...

        return crud.create(customer);
    }

    /**
     * Updates an existing customer after validating data.
     * @param customer The Customer object with updated information.
     * @return The updated Customer object.
     * @throws Exception If a validation error occurs.
     */
    public Customer update(Customer customer) throws Exception {
        log.info("CustomerService.update() - Updating " + customer.getFirstName() + " " + customer.getLastName());
        validator.validateCustomer(customer);

        // Additional business logic, if needed...

        return crud.update(customer);
    }

    /**
     * Deletes a customer and cascades deletions to associated bookings.
     * @param customer The Customer object to delete.
     * @return The deleted Customer object, or null if not found.
     */
    public Customer delete(Customer customer) throws Exception {
        log.info("CustomerService.delete() - Deleting " + customer);

        if (customer.getId() != null) {
            return crud.delete(customer);  // Cascade deletions handled in Customer entity
        } else {
            log.info("CustomerService.delete() - No ID found, cannot delete.");
            return null;
        }
    }
}
