
<style>
/* General Styles */
body {
    font-family: 'Arial', sans-serif;
    line-height: 1.6;
    margin: 20px;
    color: #333;
    background-color: #f0f8ff;
}

h1, h2, h3 {
    color: #0056b3;
    margin-top: 20px;
    margin-bottom: 10px;
}

h1 {
    border-bottom: 2px solid #0056b3;
    padding-bottom: 5px;
}

/* Student Info Box */
.student-info {
    background-color: #e8f0fe;
    border: 1px solid #b3d4fc;
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.student-info p {
    margin: 5px 0;
}

/* Feature List Styles */
.feature-list {
    list-style-type: disc;
    margin-left: 30px;
}

/* Code Block Styles */
pre {
    background-color: #f4f4f4;
    border: 1px solid #ddd;
    padding: 10px;
    overflow: auto;
    border-radius: 5px;
}

code {
    font-family: 'Courier New', monospace;
    color: #333;
}

/* Table Styles */
table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

th, td {
    padding: 12px 15px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

th {
    background-color: #f2f2f2;
    color: #333;
    font-weight: bold;
}

tr:hover {
    background-color: #f5f5f5;
}

/* Link Styles */
a {
    color: #007bff;
    text-decoration: none;
}

a:hover {
    text-decoration: underline;
}

/* Strong Emphasis Styles */
strong {
    color: #c0392b; /* A shade of red for emphasis */
}

/* Blockquote Styles */
blockquote {
    border-left: 5px solid #ccc;
    padding: 10px;
    margin: 1.5em 10px;
    background: #f9f9f9;
    font-style: italic;
    color: #666;
}

/* Horizontal Rule Styles */
hr {
    border: 0;
    height: 1px;
    background: #ccc;
    margin: 20px 0;
}

/* Image Styles */
img {
    max-width: 100%;
    height: auto;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* List Styles */
ul, ol {
    margin-bottom: 20px;
}

/* Task List Styles */
ul.task-list {
    list-style-type: none;
    padding-left: 20px;
}

ul.task-list li {
    padding-left: 0;
    text-indent: -20px;
}

ul.task-list input[type="checkbox"] {
    margin-right: 10px;
}

/* Definition List Styles */
dl {
    margin-bottom: 20px;
}

dt {
    font-weight: bold;
    margin-top: 10px;
}

dd {
    margin-left: 20px;
    margin-bottom: 10px;
}

/* Alert Box Styles */
.alert {
    padding: 15px;
    margin-bottom: 20px;
    border: 1px solid transparent;
    border-radius: 4px;
}

.alert-success {
    color: #3c763d;
    background-color: #dff0d8;
    border-color: #d6e9c6;
}

.alert-info {
    color: #31708f;
    background-color: #d9edf7;
    border-color: #bce8f1;
}

.alert-warning {
    color: #8a6d3b;
    background-color: #fcf8e3;
    border-color: #faebcc;
}

.alert-danger {
    color: #a94442;
    background-color: #f2dede;
    border-color: #ebccd1;
}

/* Button Styles */
.button {
    background-color: #4CAF50; /* Green */
    border: none;
    color: white;
    padding: 10px 20px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
    border-radius: 5px;
}

.button:hover {
    background-color: #3e8e41;
}

/* Details and Summary Styles */
details {
    margin-bottom: 20px;
    border: 1px solid #ddd;
    padding: 10px;
    border-radius: 5px;
}

summary {
    font-weight: bold;
    cursor: pointer;
    padding: 5px;
}

/* Horizontal Scrolling Container */
.horizontal-scroll {
    overflow-x: auto;
    white-space: nowrap;
    padding: 10px 0;
}

/* Fancy Divider */
.fancy-divider {
    border: 0;
    height: 3px;
    background-image: linear-gradient(to right, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.75), rgba(0, 0, 0, 0));
    margin: 20px 0;
}

/* Responsive Image Grid */
.image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
}

/* Card Styles */
.card {
    border: 1px solid #ddd;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.card-header {
    background-color: #f2f2f2;
    padding: 10px;
    font-weight: bold;
}

.card-body {
    padding: 10px;
}

.card-footer {
    background-color: #f9f9f9;
    padding: 10px;
    text-align: right;
}
</style>

<div class="student-info">
    <p><strong>Name:</strong> Birat Gautam </p>
    <p><strong>University:</strong> Birmingham City University</p>
    <p><strong>Module:</strong> Object Oriented Programming</p>
    <p><strong>Student ID:</strong> 24128425</p>
</div>

# Flight Booking System

A robust Java-based flight booking system with both CLI and GUI interfaces, offering comprehensive management of flights, customers, and bookings with dynamic pricing and real-time validation. This system implements all requirements to achieve a grade of 80% or more.

### **[GitHub Link](https://github.com/ToniBirat7/Flight_Management_System_Java_v2.git)**

## 🌟 Features

### Flight Management
- Add and manage flights with detailed information:
  - Flight number, origin, destination
  - Departure date
  - Seat capacity
  - Base price
- Dynamic pricing based on:
  - Days until departure
  - Current occupancy rate
  - Seasonal factors
- Hide/remove flights without deleting records
- Automatic filtering of departed flights
- Real-time seat availability tracking

### Customer Management
- Complete customer profile management:
  - Personal details (name, phone, email)
  - Booking history
  - Account status
- Input validation for:
  - Phone numbers (11-digit format)
  - Email addresses (standard format)
- Soft delete functionality
- Privacy-focused data handling

### Booking System
- Comprehensive booking features:
  - New booking creation
  - Booking modifications
  - Cancellation handling
- Smart pricing system:
  - Dynamic price calculation
  - Cancellation fees
  - Rebooking charges
- Capacity management:
  - Real-time seat availability
  - Overbooking prevention
  - Waitlist handling

### Data Persistence
- Reliable file-based storage system:
  - Separate files for flights, customers, and bookings
  - Atomic file operations
  - Data integrity checks
- Automatic data recovery
- Transaction rollback on failures

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Any Java IDE (Eclipse, IntelliJ IDEA, etc.)

### Installation
1. Clone the repository:

```bash
git clone https://github.com/yourusername/flight-booking-system.git
```

2. Open the project in your preferred IDE
3. Build and run the application

### Running the Application
- **CLI Mode**: Run `Main.java` with command line arguments
- **GUI Mode**: Run `MainWindow.java` for the graphical interface

## 💻 Usage

### Command Line Interface

| Command                                   | Description                                                                                        |
| ----------------------------------------- | -------------------------------------------------------------------------------------------------- |
| `listflights`                             | Print all flights (future and not departed)                                                        |
| `listcustomers`                           | Print all customers                                                                                |
| `addflight`                               | Add a new flight                                                                                   |
| `addcustomer`                             | Add a new customer                                                                                 |
| `showflight [flight id]`                  | Show flight details including list of passengers (name, phone number)                              |
| `showcustomer [customer id]`              | Show customer details including list of bookings (flight number, origin, destination, date, price) |
| `addbooking [customer id] [flight id]`    | Add a new booking for the customer on the flight (if capacity is available)                        |
| `cancelbooking [customer id] [flight id]` | Cancel a booking for the customer on the flight (updates booking status and flight passenger list) |
| `editbooking [booking id] [flight id]`    | Update a booking for a particular booking ID, assigning to another available flight ID             |
| `loadgui`                                 | Loads the GUI version                                                                              |
| `help`                                    | Prints help message                                                                                |
| `exit`                                    | Exits the program                                                                                  |

### Graphical User Interface
The GUI provides intuitive access to all features through:
- Menu-driven navigation
- Form-based data entry
- Interactive lists and tables
- Pop-up dialogs for actions
- Real-time validation feedback
- Popup window to display list of passengers for a selected flight
- Popup window showing Booking details for selected customer from list of all customers
- "Add" Customer functionality with a popup form
- Delete Flight and Delete Customer functionalities through GUI.
- Implemented GUI booking, canceling, and updating bookings.

## System Implementation Details

This section provides details on implemented functionality required for higher marking bands.

**Data Structures:**

*   **Customer:** Stores customer ID, Name, Phone Number, Email, and List of Bookings.  Uses a `boolean isDeleted` flag for soft deletion instead of complete removal.
*   **Flight:** Stores Flight Number, Origin, Destination, Departure Date, Seat Capacity, Base Price, `boolean isDeleted` and the list of bookings (with cancellation rebook fees.)
*   **Booking:** Stores Booking ID, references Customer and Flights, calculated price for a specific flight

**Core Functionalities:**

*   **Add New Customers:** Stores Customer ID, Name, Phone Number and a list of bookings.
*   **List All Customers:** Implemented functionality to list all customers.
*   **Issue Bookings:** Booking object with references to Flights and Customers. Customer is also added to the list of flights object’s passenger in this particular flight.
*   **Cancel Bookings:** Updates the Booking Status as "canceled". Remove’s this particular Customer in Flights’ Object customer list (for passenger count to be updated).
*   **`showcustomer` Command:** Displays a particular customers info along with the Booking Details (flight number, origin, destination, date and price).
*   **`showflight` Command:** Displays a Particular flight including details for Passengers (name, phone number.)
*   **Save/Load from Backend:** Upon closing, application saves system details on different files `(flights.txt, customers.txt, bookings.txt)`, when the system initializes the information is retrieved in order and repopulated inside of our application.

**Marks of 50% to maximum of 59% Implemented Functionality:**

*   `Number of seats`: The system can capture seat Capacity using `addflight`. It can retrieve all capacity related in `List flights.` Stored in text storage after exiting the app or can persist automatically whenever a flight command runs. Finally upon re-entering application we confirm if the value has correctly stored after being retrieved inside program's data structure
*   `Email Attribute`: Just as number of seat's , `email` is validated upon adding to Customer Class. It can successfully retrieved when all commands have reloaded , and on program reinitialize we validated its functionality

**Marks of 60% to maximum of 69% Implemented Functionality:**

* GUI Extension for displaying Customers from list selected on table flight table for a list passengers list. This feature is similar on `Show Flight.`

**Marks of 70% to maximum of 79% Implemented Functionality:**

*   **Remove (Hide) Flights and Customers:** Implemented using `isDeleted` flags in `Flight` and `Customer` classes.  The `listflights` and `listcustomers` commands only return non-deleted entities.  Extend the implementation for the GUI application to add a Delete functionality for both flights and customers using the GUI.
*   **Maximum Passenger Limit:** Enforced passenger limit on flight booking. The `addbooking` command checks the `capacity` property of the `Flight` class.  An error message is displayed if the flight is full.
*   Add Javadoc documentation to new methods created.

**Marks of 80% and over Implemented Functionality:**

*   **List Future Flights Only:** The `listflights` command now only lists flights with departure dates in the future. We filter using the  systemDate, if the scheduled flight time date occurs previous than program current clock time of current time zone (where is currently launched.), its not loaded into app
*   **Cancellation/Rebook Fees:**  Cancellation/rebook fees are added to Bookings on cancellations or updates respectively. Those new bookings can display booking amount after applying calculation. Also that bookings on Flight Booking are calculated dynamically.
*   **Dynamic Flight Pricing:**
    * Prices Adjust based on day, distance ,time and occupancy rates in booking (for now,) to complete calculations

## 🏗 Architecture

### Design Patterns
- **Command Pattern**: For operation encapsulation
- **MVC Pattern**: For separation of concerns
- **DAO Pattern**: For data access abstraction

### Key Components
1. **Model Layer**: Core business logic
2. **Command Layer**: Operation handling
3. **Data Layer**: Persistence management
4. **GUI Layer**: User interface

## 🔒 Data Validation

### Customer Data
- Phone number format (11 digits)
- Valid email address format
- Required fields validation

### Flight Data
- Future date validation
- Capacity constraints
- Price range validation

### Booking Data
- Seat availability
- Customer eligibility
- Date restrictions

## 🛠 Advanced Features

### Dynamic Pricing
Prices adjust based on:
- Proximity to departure (last-minute pricing)
- Seat occupancy rates
- Seasonal factors

### Booking Management
- Cancellation handling with fees
- Rebooking with price adjustments
- Booking history tracking

### System Security
- Data validation at multiple levels
- Atomic file operations
- Transaction rollback capability
- Data file persistance.

## 📝 Documentation
- Comprehensive Javadoc documentation
- Inline code comments
- User guides for both CLI and GUI

## 🤝 Contributing
Contributions are welcome! Please feel free to submit pull requests.

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.