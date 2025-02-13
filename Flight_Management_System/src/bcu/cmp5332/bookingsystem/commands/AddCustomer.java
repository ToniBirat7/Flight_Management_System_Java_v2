package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class AddCustomer implements Command {

    private final String name;
    private final String phone;
    private final String email;

    public AddCustomer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new FlightBookingSystemException("Invalid email format");
        }
        
        // Validate phone format
        if (!phone.matches("^\\d{11}$")) {
            throw new FlightBookingSystemException("Phone number must be 11 digits");
        }
        
        // Find the next available ID, including deleted customers
        int nextId = 1;
        for (Customer customer : flightBookingSystem.getAllCustomers()) {
            if (customer.getId() >= nextId) {
                nextId = customer.getId() + 1;
            }
        }
        
        Customer customer = new Customer(nextId, name, phone, email);
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added.");
    }
}
