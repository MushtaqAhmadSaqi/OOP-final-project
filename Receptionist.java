import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
public class Receptionist extends Employee{
    private final String receptID;
    private final String passCode;
    private ArrayList<Booking> bookings;

    public Receptionist(String name, int contactNumber, double Salary, String receptID, String passCode) {
        super(name, contactNumber, Salary);
        this.receptID = receptID;
        this.passCode = passCode;
        this.bookings = FileManager.loadFromFile("bookings.ser");
    }
    
    public String getReceptID() {
        return receptID;
    }

    public String getPassCode() {
        return passCode;
    }

    public void checkIn(Room room, Guest guest) {
        Booking booking = new Booking(room, guest);
        booking.getRoom().setStatus(false);
        bookings.add(booking);

        System.out.println("Guest Checked-In Successfully!\n");
    }
        

    public int checkOut(String guestID) {
    
        for (int i = bookings.size() - 1; i >= 0; i--) {
            Booking b = bookings.get(i);
    
            if (b.getGuests().getGuestID().equals(guestID)) {
                
                String billReceipt = generateBill(b); 
                System.out.println(billReceipt);
                
                int roomNumber = b.getRoom().getRoomNumber();
                b.getRoom().setStatus(true);
                bookings.remove(i);
                System.out.println("Guest Checked-out Successfully!");
                return roomNumber; 
            }
        }
    
        System.out.println("Guest [" + guestID + "] not found.");
        return -1;
    }

    
    public String generateBill(Booking b) {
        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(b.getCheckInDate(), today);
        if (days == 0) {
            days = 1;
        }
        double totalAmount = b.getRoom().calculatePrice((int)days);
    
        StringBuilder sb = new StringBuilder();
        sb.append("\n============= BILL RECEIPT =============\n");
        sb.append("Guest Name   : ").append(b.getGuests().getName()).append("\n");
        sb.append("Room Number  : ").append(b.getRoom().getRoomNumber()).append("\n");
        sb.append("Check-in     : ").append(b.getCheckInDate()).append("\n");
        sb.append("Check-out    : ").append(today).append("\n");
        sb.append("Total Days   : ").append(days).append("\n");
        sb.append("Total Amount : RS.").append(totalAmount).append("\n");
        sb.append("========================================\n");
        
        return sb.toString(); 
    }

    public void modifyGuestInBooking(int roomNumber, Guest newGuest) {
    for (Booking b : bookings) {
        if ((b.getRoom()).getRoomNumber() == roomNumber) {
            b.setGuests(newGuest); 
            System.out.println("Guest in room " + roomNumber + " updated successfully.");
            return;
        }
    }
        System.out.println("Booking for room " + roomNumber + " not found.");
    }

   
    public void displayGuestHistory(String guestID) {
        for (Booking book : bookings) {
            if (book.getGuests().getGuestID().equals(guestID)) {
                System.out.println(book.getGuests());
            }
        }
    }

    public void displayActiveGuestIDs() {
        System.out.print("Active Guest IDs: [ ");
        for (Booking b : bookings) {
            System.out.print(b.getGuests().getGuestID() + " "); 
        }
        System.out.println("]");
    }

    public void displayRoomsNo() {
        System.out.println("Room No of booked rooms : ");
        for (Booking booking : bookings) {
            System.out.println(booking.getRoom().getRoomNumber());
        }
    }
   
    public void displayBookingHistory() {
        System.out.println("=== Booking History ===");
        for (Booking book : bookings) {
            System.out.println();
            System.out.println(book);
        }
    }

    public void searchBooking(String name) {
        for (Booking book : bookings) {
            if (book.getGuests().getName().equalsIgnoreCase(name)) {
                System.out.println(book);
                return;
            }
        }
        System.out.println("Booking not found!");

    }
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Receptionist)) {
            return false;
        }
        Receptionist recp = (Receptionist) obj;
        return this.receptID != null && receptID.equals(recp.receptID);
   
    }

    public void save() {
        FileManager.saveToFile("bookings.ser", bookings);
    }

    public void load() {
        bookings = FileManager.loadFromFile("bookings.ser");
    }

    public ArrayList<Booking> getBookings() { return bookings; }
}