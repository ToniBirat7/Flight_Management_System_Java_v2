package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingDataManager implements DataManager {

    private final String FILENAME = "resources/data/bookings.txt";

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        Path path = Paths.get(FILENAME);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        processLine(line.trim(), fbs);
                    } catch (FlightBookingSystemException e) {
                        System.err.printf("Warning: Skipping invalid booking on line %d: %s%n",
                                lineNumber, e.getMessage());
                    }
                }
            }
        }
    }

    private void processLine(String line, FlightBookingSystem fbs) throws FlightBookingSystemException {
        String[] parts = line.split("::");
        if (parts.length < 7) {
            throw new FlightBookingSystemException("Invalid booking data format: " + line);
        }

        try {
            int bookingId = Integer.parseInt(parts[0]);
            int customerId = Integer.parseInt(parts[1]);
            int flightId = Integer.parseInt(parts[2]);
            LocalDate bookingDate = LocalDate.parse(parts[3]);
            boolean isActive = Boolean.parseBoolean(parts[4]);
            double bookedPrice = Double.parseDouble(parts[5]);
            double totalPrice = Double.parseDouble(parts[6]);

            // Get customer and flight
            Customer customer = fbs.getCustomerByID(customerId);
            Flight flight = fbs.getFlightByID(flightId);

            if (customer == null || customer.isDeleted()) {
                throw new FlightBookingSystemException("Customer not found or deleted");
            }
            if (flight == null || flight.isDeleted()) {
                throw new FlightBookingSystemException("Flight not found or deleted");
            }

            // Create booking with proper initialization
            Booking booking = new Booking(customer, flight, bookingDate, bookedPrice);
            booking.setId(bookingId);
            
            if (!isActive) {
                booking.cancel("Loaded from file as cancelled");
            }

            customer.addBooking(booking);
            if (isActive && !flight.hasDeparted(fbs.getSystemDate())) {
                flight.addPassenger(customer);
            }

        } catch (NumberFormatException | DateTimeParseException ex) {
            throw new FlightBookingSystemException("Invalid data format: " + line);
        }
    }

    private String formatPriceBreakdown(Booking booking) {
        StringBuilder sb = new StringBuilder();
        double originalPrice = booking.getBookedPrice();
        double totalPrice = booking.getTotalPrice();
        
        if (!booking.isActive()) {
            sb.append("CANCELLED - ");
        }
        
        if (totalPrice == originalPrice) {
            return String.format("£%.2f", totalPrice);
        }
        
        return String.format("£%.2f → £%.2f", originalPrice, totalPrice);
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Customer customer : fbs.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                String line = String.format("%d::%d::%d::%s::%b::%.2f::%.2f",
                    booking.getId(),
                    customer.getId(),
                    booking.getFlight().getId(),
                    booking.getBookingDate(),
                    booking.isActive(),
                    booking.getBookedPrice(),
                    booking.getTotalPrice()
                );
                lines.add(line);
            }
        }
        // Write all bookings to file atomically
        Path path = Paths.get(FILENAME);
        Files.createDirectories(path.getParent());
        Path tempFile = path.resolveSibling(path.getFileName() + ".tmp");
        Files.write(tempFile, lines);
        Files.move(tempFile, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}
