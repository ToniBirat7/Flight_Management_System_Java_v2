package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.temporal.ChronoUnit;

/**
 * Represents a flight in the booking system
 * Contains flight details and manages passenger list
 */
public class Flight {

    /**
     * Unique identifier for the flight
     */
    private final int id;

    /**
     * Flight number/code
     */
    private final String flightNumber;

    /**
     * Origin airport/city
     */
    private final String origin;

    /**
     * Destination airport/city
     */
    private final String destination;

    /**
     * Date of departure
     */
    private final LocalDate departureDate;

    /**
     * Maximum number of passengers
     */
    private final int capacity;

    /**
     * Ticket price
     */
    private final double price;

    /**
     * Deletion status flag
     */
    private boolean isDeleted;

    /**
     * Set of passengers on this flight
     */
    private final Set<Customer> passengers;

    /**
     * Creates a new Flight with the specified details
     * 
     * @param id            unique identifier
     * @param flightNumber  flight number/code
     * @param origin        departure location
     * @param destination   arrival location
     * @param departureDate date of departure
     * @param capacity      maximum number of passengers
     * @param price         ticket price
     */
    public Flight(int id, String flightNumber, String origin, String destination,
            LocalDate departureDate, int capacity, double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
        this.isDeleted = false;
        this.passengers = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted() {
        this.isDeleted = true;
    }

    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }

    public int getAvailableSeats() {
        return capacity - passengers.size();
    }

    public void addPassenger(Customer passenger) throws FlightBookingSystemException {
        if (passengers.size() >= capacity) {
            throw new FlightBookingSystemException("Flight is full - Capacity: " + capacity);
        }

        // Check if passenger is already on the flight
        if (passengers.contains(passenger)) {
            throw new FlightBookingSystemException("Customer is already a passenger on this flight");
        }

        passengers.add(passenger);
    }

    public void removePassenger(Customer passenger) throws FlightBookingSystemException {
        if (!passengers.contains(passenger)) {
            throw new FlightBookingSystemException("Customer is not a passenger on this flight");
        }
        passengers.remove(passenger);
    }

    public boolean hasAvailableSeats() {
        return getAvailableSeats() > 0;
    }

    public boolean isPassenger(Customer customer) {
        return passengers.contains(customer);
    }

    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return String.format("Flight #%d - %s - %s to %s on %s - £%.2f - %d/%d seats",
                id, flightNumber, origin, destination, departureDate.format(dtf),
                price, passengers.size(), capacity);
    }

    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDetailsShort());
        sb.append("\nCapacity: ").append(capacity);
        sb.append("\nPrice: £").append(String.format("%.2f", price));
        sb.append("\nPassengers: ").append(passengers.size());
        for (Customer passenger : passengers) {
            sb.append("\n\t").append(passenger.getDetailsShort());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Flight #%d - %s - %s to %s - %s - %d passengers",
                id, flightNumber, origin, destination, departureDate, passengers.size());
    }

    /**
     * Calculates the current price based on various factors
     * 
     * @param systemDate current system date
     * @return calculated price
     */
    public double calculateCurrentPrice(LocalDate systemDate) {
        double multiplier = 1.0;

        // Calculate days until departure
        long daysUntilDeparture = ChronoUnit.DAYS.between(systemDate, departureDate);
        
        // Debug line to verify calculation
        System.out.println("\nPrice calculation for flight " + flightNumber + ":");
        System.out.println("System date: " + systemDate);
        System.out.println("Departure date: " + departureDate);
        System.out.println("Days until departure: " + daysUntilDeparture);

        // Price increases as departure date approaches
        if (daysUntilDeparture <= 7) {
            multiplier += 0.5; // +50% for last week
            System.out.println("Last week booking: +50%");
        } else if (daysUntilDeparture <= 30) {
            multiplier += 0.25; // +25% for last month
            System.out.println("Last month booking: +25%");
        }

        // Price increases as seats fill up
        double occupancyRate = 1.0 - ((double) getAvailableSeats() / capacity);
        System.out.println("Occupancy rate: " + String.format("%.1f%%", occupancyRate * 100));

        if (occupancyRate >= 0.8) {
            multiplier += 0.3; // +30% when 80%+ full
            System.out.println("High demand: +30%");
        } else if (occupancyRate >= 0.5) {
            multiplier += 0.15; // +15% when 50%+ full
            System.out.println("Medium demand: +15%");
        }

        double finalPrice = Math.round(price * multiplier * 100.0) / 100.0;
        System.out.println(String.format("Base price: £%.2f, Final price: £%.2f (Multiplier: %.2f)\n",
            price, finalPrice, multiplier));

        return finalPrice;
    }

    /**
     * Checks if the flight has departed based on system date
     * 
     * @param systemDate current system date
     * @return true if flight has departed
     */
    public boolean hasDeparted(LocalDate systemDate) {
        return departureDate.isBefore(systemDate);
    }
}
