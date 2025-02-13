package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FlightDataManager implements DataManager {
    
    private final String FILENAME = "resources/data/flights.txt";
    
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        Path path = Paths.get(FILENAME);
        if (!Files.exists(path)) {
            // Create empty file if it doesn't exist
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
                        System.err.printf("Warning: Skipping invalid flight on line %d: %s%n", 
                            lineNumber, e.getMessage());
                    }
                }
            }
        }
    }
    
    private void processLine(String line, FlightBookingSystem fbs) throws FlightBookingSystemException {
        String[] parts = line.split("::");
        if (parts.length != 7) {
            throw new FlightBookingSystemException("Invalid flight data format: " + line);
        }
        
        try {
            int id = Integer.parseInt(parts[0]);
            String flightNumber = parts[1].trim();
            String origin = parts[2].trim();
            String destination = parts[3].trim();
            LocalDate departureDate = LocalDate.parse(parts[4].trim());
            int capacity = Integer.parseInt(parts[5]);
            double price = Double.parseDouble(parts[6]);
            
            // Validate data
            if (flightNumber.isEmpty()) {
                throw new FlightBookingSystemException("Flight number cannot be empty: " + line);
            }
            if (origin.isEmpty() || destination.isEmpty()) {
                throw new FlightBookingSystemException("Origin and destination cannot be empty: " + line);
            }
            if (origin.equalsIgnoreCase(destination)) {
                throw new FlightBookingSystemException("Origin and destination cannot be the same: " + line);
            }
            if (departureDate.isBefore(fbs.getSystemDate())) {
                throw new FlightBookingSystemException("Flight date cannot be in the past: " + line);
            }
            if (capacity <= 0) {
                throw new FlightBookingSystemException("Capacity must be positive: " + line);
            }
            if (price <= 0) {
                throw new FlightBookingSystemException("Price must be positive: " + line);
            }
            
            // Check for duplicate flight ID
            try {
                fbs.getFlightByID(id);
                throw new FlightBookingSystemException("Duplicate flight ID: " + id);
            } catch (FlightBookingSystemException e) {
                // Expected - flight doesn't exist yet
            }
            
            // Check for duplicate flight number on same date
            for (Flight existing : fbs.getFlights()) {
                if (existing.getFlightNumber().equals(flightNumber) && 
                    existing.getDepartureDate().equals(departureDate)) {
                    throw new FlightBookingSystemException(
                        "Duplicate flight number on same date: " + flightNumber);
                }
            }
            
            Flight flight = new Flight(id, flightNumber, origin, destination, 
                departureDate, capacity, price);
            fbs.addFlight(flight);
            
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid number format in flight data: " + line);
        } catch (DateTimeParseException ex) {
            throw new FlightBookingSystemException("Invalid date format in flight data: " + line);
        }
    }
    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        // Ensure directory exists
        Path path = Paths.get(FILENAME);
        Files.createDirectories(path.getParent());
        
        List<String> lines = new ArrayList<>();
        
        // Only store non-deleted flights
        for (Flight flight : fbs.getFlights()) {
            if (!flight.isDeleted()) {
                String line = String.format("%d::%s::%s::%s::%s::%d::%.2f::",
                    flight.getId(),
                    flight.getFlightNumber(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureDate(),
                    flight.getCapacity(),
                    flight.getPrice());
                lines.add(line);
            }
        }
        
        // Write all flights to file atomically
        Path tempFile = path.resolveSibling(path.getFileName() + ".tmp");
        Files.write(tempFile, lines);
        Files.move(tempFile, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}
