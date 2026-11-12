# Hotel Management System

A Java hotel management system with both a console interface and a Java Swing GUI. The application separates manager and receptionist responsibilities, supports room and booking management, and saves data between runs using Java serialization.

## Features

### Manager

- Add, remove, search, and display rooms
- Manage hotel staff members
- View room type, price, capacity, and availability
- Prevent removal of rooms that are currently booked

### Receptionist

- Check guests in and out
- Search bookings and guests
- Modify and display bookings
- Display available rooms
- Generate a bill when a guest checks out
- Validate room availability, guest capacity, and duplicate active guests

### Application

- Role-based login for managers and receptionists
- Swing GUI with separate manager and receptionist dashboards
- Console-based menu system
- Persistent rooms, staff, and bookings
- Single, double, and family room types

## Requirements

- Java Development Kit (JDK) 8 or later
- A terminal or Java IDE

## Running the application

All source files are in the default Java package. From the project directory, compile the application:

```bash
javac *.java
```

### Launch the GUI

```bash
java HotelMainGUI
```

The GUI starts with the following credentials:

| Role | User ID | Password |
| --- | --- | --- |
| Manager | `Saqi` | `123` |
| Receptionist | `Saqi` | `123` |

### Launch the console application

```bash
java Runner
```

The console entry point currently uses these credentials:

| Role | User ID | Password |
| --- | --- | --- |
| Manager | `saqi` | `123` |
| Receptionist | `saad01` | `123` |

## Data storage

The application creates these files in the working directory when data is saved:

- `rooms.ser` - room records
- `staff.ser` - staff records
- `bookings.ser` - booking records

If these files do not exist, the application starts with empty collections. Keep the files in the same directory from which the application is launched.

## Project structure

| File | Responsibility |
| --- | --- |
| `HotelMainGUI.java` | Swing GUI entry point and role-based dashboards |
| `Runner.java` | Console application entry point |
| `Hotel.java` | Console menus, authentication, and hotel workflows |
| `Manager.java` | Room and staff management |
| `Receptionist.java` | Guest check-in, check-out, and booking management |
| `Room.java` | Room abstraction and single, double, and family room implementations |
| `Booking.java` | Booking model and check-in date |
| `Guest.java` | Guest model |
| `Staff.java` | Staff model |
| `Employee.java` | Employee base class |
| `Person.java` | Common person base class |
| `FileManager.java` | Serialized data loading and saving |
| `Input.java` | Shared console input scanner |

## Object-oriented concepts

The project demonstrates:

- Inheritance through `Person`, `Employee`, `Manager`, and `Receptionist`
- Abstraction through the `Room` base class
- Polymorphism through the different room implementations
- Encapsulation through model fields and accessor methods
- Serialization for persistent application data
