package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataManager implements DataManager {
    
    private final String FILENAME = "resources/data/customers.txt";
    
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
                        System.err.printf("Warning: Skipping invalid customer on line %d: %s%n", 
                            lineNumber, e.getMessage());
                    }
                }
            }
        }
    }
    
    private void processLine(String line, FlightBookingSystem fbs) throws FlightBookingSystemException {
        String[] parts = line.split("::");
        if (parts.length != 4) {
            throw new FlightBookingSystemException("Invalid customer data format: " + line);
        }
        
        try {
            int id = Integer.parseInt(parts[0]);
            String name = parts[1].trim();
            String phone = parts[2].trim();
            String email = parts[3].trim();
            
            // Validate data
            if (name.isEmpty()) {
                throw new FlightBookingSystemException("Customer name cannot be empty: " + line);
            }
            if (!phone.matches("^\\d{11}$")) {
                throw new FlightBookingSystemException("Invalid phone number format (must be 11 digits): " + line);
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new FlightBookingSystemException("Invalid email format: " + line);
            }
            
            // Check for duplicate customer ID
            try {
                fbs.getCustomerByID(id);
                throw new FlightBookingSystemException("Duplicate customer ID: " + id);
            } catch (FlightBookingSystemException e) {
                // Expected - customer doesn't exist yet
            }
            
            Customer customer = new Customer(id, name, phone, email);
            fbs.addCustomer(customer);
            
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid customer ID: " + line);
        }
    }
    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        // Ensure directory exists
        Path path = Paths.get(FILENAME);
        Files.createDirectories(path.getParent());
        
        List<String> lines = new ArrayList<>();
        
        // Only store non-deleted customers
        for (Customer customer : fbs.getCustomers()) {
            if (!customer.isDeleted()) {
                String line = String.format("%d::%s::%s::%s::",
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getEmail());
                lines.add(line);
            }
        }
        
        // Write all customers to file atomically
        Path tempFile = path.resolveSibling(path.getFileName() + ".tmp");
        Files.write(tempFile, lines);
        Files.move(tempFile, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}
