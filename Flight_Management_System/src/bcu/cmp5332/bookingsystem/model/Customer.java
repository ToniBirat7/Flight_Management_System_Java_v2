package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the booking system
 * Manages customer details and their bookings
 */
public class Customer {
    
    /**
     * Unique identifier for the customer
     */
    private final int id;
    
    /**
     * Customer's full name
     */
    private final String name;
    
    /**
     * Customer's phone number
     */
    private final String phone;
    
    /**
     * Customer's email address
     */
    private final String email;
    
    /**
     * Deletion status flag
     */
    private boolean isDeleted;  // New field
    
    /**
     * List of customer's bookings
     */
    private final List<Booking> bookings;
    
    /**
     * Creates a new Customer with the specified details
     * @param id unique identifier
     * @param name customer's full name
     * @param phone customer's phone number
     * @param email customer's email address
     */
    public Customer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isDeleted = false;  // New customers are not deleted by default
        this.bookings = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    /**
     * Checks if the customer is marked as deleted
     * @return true if the customer is deleted, false otherwise
     */
    public boolean isDeleted() {
        return isDeleted;
    }
    
    /**
     * Marks the customer as deleted
     */
    public void setDeleted() {
        this.isDeleted = true;
    }
    
    /**
     * Gets the list of bookings for this customer
     * @return unmodifiable list of bookings
     */
    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }
    
    /**
     * Adds a booking to the customer's bookings
     * @param booking the booking to add
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    /**
     * Gets a string representation of the customer
     * @return string with customer details
     */
    @Override
    public String toString() {
        return String.format("Customer #%d - %s - %s", id, name, email);
    }
    
    public String getDetailsShort() {
        return String.format("Customer #%d - %s - %s - %s", id, name, phone, email);
    }
    
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer #").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Bookings: ").append(bookings.size()).append("\n");
        for (Booking booking : bookings) {
            sb.append("\t").append(booking.getFlight().getDetailsShort()).append("\n");
        }
        return sb.toString();
    }
}
