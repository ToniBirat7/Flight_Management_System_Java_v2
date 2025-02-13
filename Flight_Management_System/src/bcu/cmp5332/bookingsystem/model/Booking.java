package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a booking in the system with price history and fees
 * Links customers with flights and manages booking status and pricing
 */
public class Booking {

    /** Standard cancellation fee */
    private static final double CANCELLATION_FEE = 50.0;
    /** Standard rebooking fee */
    private static final double REBOOKING_FEE = 30.0;

    private static int lastId = 0;
    /**
     * Unique identifier for the booking
     */
    private int id;

    /**
     * The customer who made the booking
     */
    private final Customer customer;

    /**
     * The flight being booked
     */
    private Flight flight;

    /**
     * Date when booking was made
     */
    private final LocalDate bookingDate;

    /**
     * Current status of the booking
     */
    private boolean isActive = true;

    /** Original price when booking was made */
    private final double originalPrice;
    /** Current price after any changes */
    private double currentPrice;
    /** Accumulated cancellation fees */
    private double cancellationFee;
    /** Accumulated rebooking fees */
    private double rebookingFee;
    /** History of price changes */
    private final List<PriceChange> priceHistory = new ArrayList<>();

    /**
     * Creates a new Booking with the specified details
     * 
     * @param customer    the customer making the booking
     * @param flight      the flight being booked
     * @param bookingDate date of booking
     */
    public Booking(Customer customer, Flight flight, LocalDate bookingDate) {
        this.id = ++lastId;
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;

        // Set initial prices
        this.originalPrice = flight.calculateCurrentPrice(bookingDate);
        this.currentPrice = originalPrice;

        // Record initial price
        priceHistory.add(new PriceChange(
                LocalDateTime.now(),
                "Initial booking",
                0,
                originalPrice));
    }

    /**
     * Creates a new Booking with specified price (used when loading from file)
     * 
     * @param customer    the customer making the booking
     * @param flight      the flight being booked
     * @param bookingDate date of booking
     * @param bookedPrice original price when booking was made
     */
    public Booking(Customer customer, Flight flight, LocalDate bookingDate, double bookedPrice) {
        this.id = ++lastId;
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.originalPrice = bookedPrice;
        this.currentPrice = bookedPrice;

        // Record initial price
        priceHistory.add(new PriceChange(
                LocalDateTime.now(),
                "Initial booking",
                0,
                originalPrice));
    }

    public int getId() {
        return id;
    }

    /**
     * Sets the booking ID (used when loading from file)
     * 
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
        lastId = Math.max(lastId, id); // Update lastId if necessary
    }

    public Customer getCustomer() {
        return customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight newFlight) {
        double oldPrice = this.currentPrice;
        this.flight = newFlight;

        // Calculate new price with rebooking fee
        double newBasePrice = newFlight.calculateCurrentPrice(LocalDate.now());
        this.currentPrice = newBasePrice + REBOOKING_FEE;

        // Record price change
        priceHistory.add(new PriceChange(
                LocalDateTime.now(),
                "Flight changed - Rebooking fee applied",
                oldPrice,
                this.currentPrice));
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Cancels the booking and applies cancellation fee
     * 
     * @param reason optional reason for cancellation
     */
    public void cancel(String reason) {
        if (!isActive) {
            return;
        }

        double oldPrice = this.currentPrice;
        this.isActive = false;
        this.currentPrice += CANCELLATION_FEE;

        // Record cancellation fee
        priceHistory.add(new PriceChange(
                LocalDateTime.now(),
                "Cancelled - " + reason,
                oldPrice,
                this.currentPrice));
    }

    /**
     * Rebooks to a new flight with rebooking fee
     * 
     * @param newFlight  the flight to rebook to
     * @param systemDate current system date for price calculation
     * @param reason     reason for rebooking
     */
    public void rebook(Flight newFlight, LocalDate systemDate, String reason) {
        if (!isActive) {
            throw new IllegalStateException("Cannot rebook cancelled booking");
        }

        double oldPrice = this.currentPrice;
        Flight oldFlight = this.flight;

        // Update flight and calculate new price
        this.flight = newFlight;
        this.currentPrice = newFlight.calculateCurrentPrice(systemDate) + REBOOKING_FEE;

        // Record price change
        priceHistory.add(new PriceChange(
                LocalDateTime.now(),
                String.format("Rebooked from %s to %s: %s",
                        oldFlight.getFlightNumber(),
                        newFlight.getFlightNumber(),
                        reason),
                oldPrice,
                this.currentPrice));
    }

    /**
     * Gets the original booked price
     * 
     * @return original price when booking was made
     */
    public double getBookedPrice() {
        return originalPrice;
    }

    /**
     * Gets the current total price including all fees
     * 
     * @return total price
     */
    public double getTotalPrice() {
        return currentPrice;
    }

    /**
     * Gets the complete price history of this booking
     * 
     * @return unmodifiable list of price changes
     */
    public List<PriceChange> getPriceHistory() {
        return Collections.unmodifiableList(priceHistory);
    }

    /**
     * Gets a detailed breakdown of the current price
     * 
     * @return formatted string with price details
     */
    public String getPriceBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Original Price: £%.2f\n", originalPrice));

        for (PriceChange change : priceHistory) {
            if (!change.reason.equals("Initial booking")) {
                double difference = change.newPrice - change.oldPrice;
                if (change.reason.contains("Rebooking fee")) {
                    sb.append(String.format("Rebooking Fee: £%.2f\n", REBOOKING_FEE));
                } else if (change.reason.contains("Cancelled")) {
                    sb.append(String.format("Cancellation Fee: £%.2f\n", CANCELLATION_FEE));
                } else {
                    sb.append(String.format("%s: £%.2f\n", change.reason, difference));
                }
            }
        }

        sb.append(String.format("Total: £%.2f", getTotalPrice()));
        return sb.toString();
    }

    public String getDetailsShort() {
        return "Booking: " + customer.getName() + " on " + flight.getFlightNumber()
                + " (" + (isActive ? "Active" : "Cancelled") + ")";
    }

    /**
     * Represents a change in the booking's price
     */
    public static class PriceChange {
        private final LocalDateTime timestamp;
        private final String reason;
        private final double oldPrice;
        private final double newPrice;

        public PriceChange(LocalDateTime timestamp, String reason,
                double oldPrice, double newPrice) {
            this.timestamp = timestamp;
            this.reason = reason;
            this.oldPrice = oldPrice;
            this.newPrice = newPrice;
        }

        @Override
        public String toString() {
            return String.format("%s - %s: £%.2f → £%.2f",
                    timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    reason, oldPrice, newPrice);
        }
    }
}
