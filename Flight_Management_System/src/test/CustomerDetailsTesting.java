package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import bcu.cmp5332.bookingsystem.model.*;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;

class CustomerDetailsTesting {
    
    private Flight flight;
    private Customer customer;
    private LocalDate departureDate;
    
    @BeforeEach
    void setUp() {
        departureDate = LocalDate.parse("2024-12-25");
        flight = new Flight(1, "BA123", "London", "Paris", departureDate, 100, 199.99);
        customer = new Customer(1, "John Doe", "07123456789", "john@example.com");
    }

    @Test
    void testFlightCapacity() throws FlightBookingSystemException {
        assertEquals(100, flight.getCapacity());
        assertEquals(100, flight.getAvailableSeats());
        
        flight.addPassenger(customer);
        assertEquals(99, flight.getAvailableSeats());
    }
    
    @Test
    void testFlightPrice() {
        assertEquals(199.99, flight.getPrice(), 0.01);
    }
    
    @Test
    void testFlightFullCapacity() {
        Flight smallFlight = new Flight(2, "BA456", "London", "Rome", departureDate, 1, 299.99);
        
        assertThrows(FlightBookingSystemException.class, () -> {
            smallFlight.addPassenger(customer);
            Customer customer2 = new Customer(2, "Jane Doe", "07987654321", "jane@example.com");
            smallFlight.addPassenger(customer2);
        });
    }
    
    @Test
    void testCustomerEmail() {
        assertEquals("john@example.com", customer.getEmail());
    }
    
    @Test
    void testCustomerDetails() {
        String details = customer.getDetailsShort();
        assertTrue(details.contains("john@example.com"));
        assertTrue(details.contains("07123456789"));
        assertTrue(details.contains("John Doe"));
    }

    @Test
    void testBookingPriceCalculation() throws FlightBookingSystemException {
        LocalDate bookingDate = LocalDate.parse("2024-11-01");
        Booking booking = new Booking(customer, flight, bookingDate);
        
        assertEquals(199.99, booking.getBookedPrice(), 0.01);
        assertTrue(booking.isActive());
        
        booking.cancel("Test cancellation");
        assertFalse(booking.isActive());
        assertTrue(booking.getTotalPrice() > booking.getBookedPrice());
    }
}
