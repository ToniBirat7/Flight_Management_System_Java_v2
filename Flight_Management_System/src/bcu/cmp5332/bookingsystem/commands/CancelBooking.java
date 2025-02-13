package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class CancelBooking implements Command {

    private final int customerId;
    private final int flightId;

    public CancelBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Booking booking = findBooking(flightBookingSystem);
        if (!booking.isActive()) {
            throw new FlightBookingSystemException("Booking is already cancelled");
        }
        
        Flight flight = booking.getFlight();
        Customer customer = booking.getCustomer();
        
        // Remove passenger from flight
        flight.removePassenger(customer);
        
        // Cancel the booking
        booking.cancel("Cancelled by user");
    }

    private Booking findBooking(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Find and cancel the booking
        boolean bookingFound = false;
        Booking booking = null;
        for (Booking b : customer.getBookings()) {
            if (b.getFlight().getId() == flightId && b.isActive()) {
                booking = b;
                bookingFound = true;
                break;
            }
        }
        
        if (!bookingFound) {
            throw new FlightBookingSystemException("No active booking found for customer #" + customerId + " on flight #" + flightId);
        }
        
        return booking;
    }
} 