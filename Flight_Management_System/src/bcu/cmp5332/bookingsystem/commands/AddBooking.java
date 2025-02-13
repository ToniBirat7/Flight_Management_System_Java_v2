package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class AddBooking implements Command {

    private final int customerId;
    private final int flightId;

    public AddBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Check if flight is in the past
        if (flight.getDepartureDate().isBefore(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("Cannot book a flight in the past");
        }
        
        // Check if flight is full
        if (flight.getPassengers().size() >= flight.getCapacity()) {
            throw new FlightBookingSystemException("Flight #" + flightId + " is already at full capacity");
        }
        
        // Check if customer already has an active booking on this flight
        for (Booking existingBooking : customer.getBookings()) {
            if (existingBooking.getFlight().getId() == flightId && existingBooking.isActive()) {
                throw new FlightBookingSystemException("Customer #" + customerId + 
                    " already has an active booking on Flight #" + flightId);
            }
        }
        
        // Create new booking
        Booking booking = new Booking(customer, flight, flightBookingSystem.getSystemDate());
        customer.addBooking(booking);
        flight.addPassenger(customer);
        
        System.out.println("Booking added for customer #" + customerId + " on flight #" + flightId);
    }
} 