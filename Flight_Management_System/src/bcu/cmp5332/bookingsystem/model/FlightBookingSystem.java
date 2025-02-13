package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FlightBookingSystem {
    
    private final List<Flight> flights = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private LocalDate systemDate;

    public FlightBookingSystem() {
        // Initialize with a specific date for the system
//        this.systemDate = LocalDate.of(2025, 2, 5); // Set to February 5, 2025
        this.systemDate = LocalDate.now();
    }

    public LocalDate getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(LocalDate systemDate) {
        this.systemDate = systemDate;
    }

    /**
     * Gets a list of all active (non-deleted) flights
     * @return unmodifiable list of active flights
     */
    public List<Flight> getFlights() {
        return flights.stream()
            .filter(f -> !f.isDeleted())
            .collect(Collectors.toUnmodifiableList());
    }
    
    /**
     * Gets a list of all customers, including deleted ones
     * @return unmodifiable list of all customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    /**
     * Gets a list of all active (non-deleted) customers
     * @return unmodifiable list of active customers
     */
    public List<Customer> getCustomers() {
        return customers.stream()
            .filter(c -> !c.isDeleted())
            .collect(Collectors.toUnmodifiableList());
    }

    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        return flights.stream()
            .filter(f -> f.getId() == id)
            .findFirst()
            .orElseThrow(() -> new FlightBookingSystemException("There is no flight with that ID."));
    }

    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        return customers.stream()
            .filter(c -> c.getId() == id)
            .findFirst()
            .orElseThrow(() -> new FlightBookingSystemException("There is no customer with that ID."));
    }

    public void addFlight(Flight flight) throws FlightBookingSystemException {
        if (flights.contains(flight)) {
            throw new IllegalArgumentException("Duplicate flight ID.");
        }
        for (Flight existing : flights) {
            if (existing.getFlightNumber().equals(flight.getFlightNumber()) 
                && existing.getDepartureDate().isEqual(flight.getDepartureDate())) {
                throw new FlightBookingSystemException("There is a flight with same "
                        + "number and departure date in the system");
            }
        }
        flights.add(flight);
    }

    public void addCustomer(Customer customer) throws FlightBookingSystemException {
        if (customers.contains(customer)) {
            throw new FlightBookingSystemException("Duplicate customer ID.");
        }
        customers.add(customer);
    }

    /**
     * Soft deletes a flight from the system
     * @param flight the flight to delete
     * @throws FlightBookingSystemException if flight is null, doesn't exist, or has active bookings
     */
    public void removeFlight(Flight flight) throws FlightBookingSystemException {
        if (flight == null) {
            throw new FlightBookingSystemException("Flight cannot be null");
        }
        
        if (!flights.contains(flight)) {
            throw new FlightBookingSystemException("Flight does not exist in the system");
        }
        
        // Check for active bookings
        boolean hasActiveBookings = flight.getPassengers().stream()
            .flatMap(c -> c.getBookings().stream())
            .filter(b -> b.getFlight().equals(flight))
            .anyMatch(Booking::isActive);
            
        if (hasActiveBookings) {
            throw new FlightBookingSystemException("Cannot remove flight with active bookings");
        }
        
        flight.setDeleted();
    }

    /**
     * Soft deletes a customer from the system
     * @param customer the customer to delete
     * @throws FlightBookingSystemException if customer is null, doesn't exist, or has active bookings
     */
    public void removeCustomer(Customer customer) throws FlightBookingSystemException {
        if (customer == null) {
            throw new FlightBookingSystemException("Customer cannot be null");
        }
        
        if (!customers.contains(customer)) {
            throw new FlightBookingSystemException("Customer does not exist in the system");
        }
        
        // Check for active bookings
        boolean hasActiveBookings = customer.getBookings().stream()
            .anyMatch(Booking::isActive);
        
        if (hasActiveBookings) {
            throw new FlightBookingSystemException("Cannot remove customer with active bookings");
        }
        
        customer.setDeleted();
    }
}
