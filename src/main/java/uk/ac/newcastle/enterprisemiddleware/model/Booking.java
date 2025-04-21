package uk.ac.newcastle.enterprisemiddleware.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * <p>This is the Domain object for Booking, representing how booking resources are handled in the database.</p>
 *
 * <p>It specifies how Bookings are retrieved from the database (with @NamedQueries) and includes constraints on fields
 * (using annotations like @NotNull, @Pattern, etc.).</p>
 */

@Entity
@NamedQueries({
     @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b ORDER BY b.bookingDate ASC"),
     @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT b FROM Booking b WHERE b.customer.id = :customerId"),
     @NamedQuery(name = Booking.FIND_BY_HOTEL, query = "SELECT b FROM Booking b WHERE b.hotel.id = :hotelId")
})
@XmlRootElement
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "hotel_id", "booking_date"}))
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_HOTEL = "Booking.findByHotel";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne()
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotNull(message = "Booking date cannot be null")
    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @NotNull
    @Size(min = 5, max = 20, message = "Status should be between 5 and 20 characters")
    @Column(name = "status")
    private String status;

    // Default constructor
    public Booking() {}

    // Constructor with parameters
    public Booking(Customer customer, Hotel hotel, LocalDate bookingDate, String status) {
        this.customer = customer;
        this.hotel = hotel;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Override equals and hashCode to ensure unique booking based on customer, hotel, and booking date
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return customer.equals(booking.customer) &&
               hotel.equals(booking.hotel) &&
               bookingDate.equals(booking.bookingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, hotel, bookingDate);
    }
}
