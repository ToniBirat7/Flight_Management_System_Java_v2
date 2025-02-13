package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class EditBooking implements Command {

    private final int bookingId;
    private final int newFlightId;

    public EditBooking(int bookingId, int newFlightId) {
        this.bookingId = bookingId;
        this.newFlightId = newFlightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Find the booking to edit
        Booking bookingToEdit = null;
        Customer bookingCustomer = null;
        
        // Search through all customers to find the booking
        for (Customer customer : flightBookingSystem.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                if (booking.getId() == bookingId) {
                    bookingToEdit = booking;
                    bookingCustomer = customer;
                    break;
                }
            }
            if (bookingToEdit != null) break;
        }
        
        if (bookingToEdit == null) {
            throw new FlightBookingSystemException("Booking #" + bookingId + " not found");
        }
        
        if (!bookingToEdit.isActive()) {
            throw new FlightBookingSystemException("Cannot edit a cancelled booking");
        }

        // Get the new flight
        Flight newFlight = flightBookingSystem.getFlightByID(newFlightId);
        Flight oldFlight = bookingToEdit.getFlight();
        
        // Validate the new flight
        if (newFlight.getDepartureDate().isBefore(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("Cannot book a flight in the past");
        }
        
        if (newFlight.getPassengers().size() >= newFlight.getCapacity()) {
            throw new FlightBookingSystemException("New flight is already at full capacity");
        }
        
        // Check if customer already has a booking on the new flight
        for (Booking existing : bookingCustomer.getBookings()) {
            if (existing.getFlight().getId() == newFlightId && existing.isActive() && existing.getId() != bookingId) {
                throw new FlightBookingSystemException("Customer already has an active booking on the new flight");
            }
        }
        
        // Update the booking
        oldFlight.removePassenger(bookingCustomer);
        newFlight.addPassenger(bookingCustomer);
        bookingToEdit.setFlight(newFlight);
        
        System.out.println("Booking #" + bookingId + " updated: Flight changed from #" + 
                          oldFlight.getId() + " to #" + newFlight.getId());
    }
} 