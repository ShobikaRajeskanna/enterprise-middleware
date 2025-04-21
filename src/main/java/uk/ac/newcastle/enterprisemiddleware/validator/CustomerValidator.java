package uk.ac.newcastle.enterprisemiddleware.validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import uk.ac.newcastle.enterprisemiddleware.Unique.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.model.Customer;
import uk.ac.newcastle.enterprisemiddleware.repository.CustomerRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Customer objects against arbitrary requirements.</p>
 *
 * <p>It validates the Customer object for constraint violations and ensures email uniqueness.</p>
 *
 * @see Customer
 * @see CustomerRepository
 * @see javax.validation.Validator
 */
@ApplicationScoped
public class CustomerValidator {

    @Inject
    Validator validator;

    @Inject
    CustomerRepository crud;

    /**
     * <p>Validates the given Customer object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.</p>
     *
     * <p>If the error is caused because an existing customer with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * @param customer The Customer object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If a customer with the same email already exists
     */
    public void validateCustomer(Customer customer) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(customer.getEmail(), customer.getId())) {
            throw new UniqueEmailException("Unique Email Violation");
        }
    }

    /**
     * <p>Checks if a customer with the same email address is already registered. This captures the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Customer class.</p>
     *
     * <p>During update, it ensures the email belongs to the record being updated.</p>
     *
     * @param email The email to check for uniqueness
     * @param id The customer id to check the email against if it exists
     * @return boolean indicating if the email already exists and doesn't belong to the provided id
     */
    boolean emailAlreadyExists(String email, Long id) {
        Customer customer = null;
        Customer customerWithID = null;
        try {
            customer = crud.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }

        if (customer != null && id != null) {
            try {
                customerWithID = crud.findById(id);
                if (customerWithID != null && customerWithID.getEmail().equals(email)) {
                    customer = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return customer != null;
    }
}
