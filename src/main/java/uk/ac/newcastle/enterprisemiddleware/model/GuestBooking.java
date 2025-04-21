package uk.ac.newcastle.enterprisemiddleware.model;

public class GuestBooking {
    private Customer customer;
    private Booking booking;

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
