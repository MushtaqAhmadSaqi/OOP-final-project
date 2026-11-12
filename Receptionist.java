import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Receptionist extends Employee {
    private static final long serialVersionUID = 1L;

    private final String receptID;
    private final String passCode;
    private ArrayList<Booking> bookings;

    public Receptionist(String name, int contactNumber, double salary, String receptID, String passCode) {
        super(name, contactNumber, salary);
        if (receptID == null || receptID.trim().isEmpty()) {
            throw new IllegalArgumentException("Receptionist ID cannot be empty.");
        }
        if (passCode == null || passCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Receptionist passcode cannot be empty.");
        }
        this.receptID = receptID.trim();
        this.passCode = passCode;
        this.bookings = FileManager.loadFromFile("bookings.ser");
        if (this.bookings == null) this.bookings = new ArrayList<>();
    }
    
    public String getReceptID() {
        return receptID;
    }

    public String getPassCode() {
        return passCode;
    }

    public void checkIn(Room room, Guest guest) {
        validateCheckIn(room, guest);

        Booking booking = new Booking(room, guest);
        room.setStatus(false);
        bookings.add(booking);

        System.out.println("Guest Checked-In Successfully!\n");
    }

    private void validateCheckIn(Room room, Guest guest) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required for check-in.");
        }
        if (guest == null) {
            throw new IllegalArgumentException("Guest is required for check-in.");
        }
        if (!room.isAvailable()) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber() + " is already booked.");
        }
        // Double-check against bookings list in case room status was desynchronized
        for (Booking b : bookings) {
            if (b.getRoom() != null && b.getRoom().getRoomNumber() == room.getRoomNumber()) {
                throw new IllegalArgumentException("Room " + room.getRoomNumber() + " already has an active booking.");
            }
        }
        if (hasActiveGuest(guest.getGuestID())) {
            throw new IllegalArgumentException("Guest with ID " + guest.getGuestID() + " is already checked in.");
        }
        if (guest.getFamilyMember() <= 0) {
            throw new IllegalArgumentException("Family members must be at least 1.");
        }
        if (guest.getFamilyMember() > room.getMaxCapacity()) {
            throw new IllegalArgumentException("Guest group has " + guest.getFamilyMember()
                    + " member(s), but room capacity is " + room.getMaxCapacity() + ".");
        }
    }

    public boolean hasActiveGuest(String guestID) {
        if (guestID == null || guestID.trim().isEmpty()) return false;
        String cleanID = guestID.trim();
        for (Booking b : bookings) {
            if (b.getGuests() != null && b.getGuests().getGuestID().equalsIgnoreCase(cleanID)) {
                return true;
            }
        }
        return false;
    }
        
    public int checkOut(String guestID) {
        if (guestID == null || guestID.trim().isEmpty()) {
            System.out.println("Invalid guest ID.");
            return -1;
        }
        String cleanID = guestID.trim();

        for (int i = bookings.size() - 1; i >= 0; i--) {
            Booking b = bookings.get(i);
    
            if (b.getGuests() != null && b.getGuests().getGuestID().equalsIgnoreCase(cleanID)) {
                String billReceipt = generateBill(b); 
                System.out.println(billReceipt);
                
                int roomNumber = (b.getRoom() != null) ? b.getRoom().getRoomNumber() : -1;
                if (b.getRoom() != null) {
                    b.getRoom().setStatus(true);
                }
                bookings.remove(i);
                System.out.println("Guest Checked-out Successfully!");
                return roomNumber; 
            }
        }
    
        System.out.println("Guest [" + cleanID + "] not found in active bookings.");
        return -1;
    }

    public String generateBill(Booking b) {
        if (b == null) {
            throw new IllegalArgumentException("Booking is required to generate a bill.");
        }

        LocalDate today = LocalDate.now();
        LocalDate inDate = (b.getCheckInDate() != null) ? b.getCheckInDate() : today;
        long days = ChronoUnit.DAYS.between(inDate, today);
        if (days <= 0) {
            days = 1;
        }

        Room r = b.getRoom();
        double totalAmount = (r != null) ? r.calculatePrice((int) Math.min(days, Integer.MAX_VALUE)) : 0.0;
    
        StringBuilder sb = new StringBuilder();
        sb.append("\n============= BILL RECEIPT =============\n");
        sb.append("Guest Name   : ").append(b.getGuests() != null ? b.getGuests().getName() : "N/A").append("\n");
        sb.append("Guest ID     : ").append(b.getGuests() != null ? b.getGuests().getGuestID() : "N/A").append("\n");
        sb.append("Room Number  : ").append(r != null ? r.getRoomNumber() : "N/A").append("\n");
        sb.append("Check-in     : ").append(inDate).append("\n");
        sb.append("Check-out    : ").append(today).append("\n");
        sb.append("Total Days   : ").append(days).append("\n");
        sb.append(String.format("Total Amount : RS.%.2f\n", totalAmount));
        sb.append("========================================\n");
        
        return sb.toString(); 
    }

    public boolean modifyGuestInBooking(int roomNumber, Guest newGuest) {
        if (newGuest == null) {
            throw new IllegalArgumentException("Guest cannot be null.");
        }

        for (Booking b : bookings) {
            if (b.getRoom() != null && b.getRoom().getRoomNumber() == roomNumber) {
                if (b.getGuests() != null && !b.getGuests().getGuestID().equalsIgnoreCase(newGuest.getGuestID())
                        && hasActiveGuest(newGuest.getGuestID())) {
                    throw new IllegalArgumentException("Guest with ID " + newGuest.getGuestID() + " is already checked in.");
                }
                if (b.getRoom() != null && newGuest.getFamilyMember() > b.getRoom().getMaxCapacity()) {
                    throw new IllegalArgumentException("Guest group exceeds room capacity of " + b.getRoom().getMaxCapacity() + ".");
                }
                b.setGuests(newGuest); 
                System.out.println("Guest in room " + roomNumber + " updated successfully.");
                return true;
            }
        }
        System.out.println("Booking for room " + roomNumber + " not found.");
        return false;
    }

    public void displayGuestHistory(String guestID) {
        if (guestID == null || guestID.trim().isEmpty()) {
            System.out.println("Invalid guest ID.");
            return;
        }
        String cleanID = guestID.trim();
        boolean found = false;
        for (Booking book : bookings) {
            if (book.getGuests() != null && book.getGuests().getGuestID().equalsIgnoreCase(cleanID)) {
                System.out.println(book.getGuests());
                if (book.getRoom() != null) {
                    System.out.println("Currently residing in Room " + book.getRoom().getRoomNumber() + " (Checked in: " + book.getCheckInDate() + ")");
                }
                found = true;
            }
        }
        if (!found) {
            System.out.println("No active bookings found for Guest ID: " + cleanID);
        }
    }

    public void displayActiveGuestIDs() {
        if (bookings.isEmpty()) {
            System.out.println("Active Guest IDs: [ None ]");
            return;
        }
        System.out.print("Active Guest IDs: [ ");
        for (Booking b : bookings) {
            if (b.getGuests() != null) {
                System.out.print(b.getGuests().getGuestID() + " "); 
            }
        }
        System.out.println("]");
    }

    public void displayRoomsNo() {
        if (bookings.isEmpty()) {
            System.out.println("No booked rooms currently.");
            return;
        }
        System.out.println("Room No of booked rooms : ");
        for (Booking booking : bookings) {
            if (booking.getRoom() != null) {
                System.out.println(booking.getRoom().getRoomNumber());
            }
        }
    }
   
    public void displayBookingHistory() {
        if (bookings.isEmpty()) {
            System.out.println("=== No Active Bookings ===");
            return;
        }
        System.out.println("=== Active Bookings (" + bookings.size() + ") ===");
        for (Booking book : bookings) {
            System.out.println();
            System.out.println(book);
        }
    }

    public void searchBooking(String query) {
        if (query == null || query.trim().isEmpty()) {
            System.out.println("Please enter a guest name or ID to search.");
            return;
        }
        String cleanQuery = query.trim();
        boolean found = false;
        for (Booking book : bookings) {
            if (book.getGuests() != null) {
                boolean nameMatch = book.getGuests().getName().equalsIgnoreCase(cleanQuery);
                boolean idMatch = book.getGuests().getGuestID().equalsIgnoreCase(cleanQuery);
                if (nameMatch || idMatch) {
                    System.out.println(book);
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("Booking not found for query: " + cleanQuery);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Receptionist)) return false;
        Receptionist recp = (Receptionist) obj;
        return receptID != null && receptID.equalsIgnoreCase(recp.receptID);
    }

    @Override
    public int hashCode() {
        return receptID != null ? receptID.toLowerCase().hashCode() : 0;
    }

    public void save() {
        FileManager.saveToFile("bookings.ser", bookings);
    }

    public void load() {
        bookings = FileManager.loadFromFile("bookings.ser");
        if (bookings == null) bookings = new ArrayList<>();
    }

    public ArrayList<Booking> getBookings() {
        if (bookings == null) bookings = new ArrayList<>();
        return bookings;
    }
}
