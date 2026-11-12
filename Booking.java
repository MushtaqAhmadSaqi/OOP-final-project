import java.io.*;
import java.time.LocalDate;
import java.util.Objects;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private Room room;
    private Guest guest;
    private LocalDate checkInDate;
    
    public Booking() {
        this.checkInDate = LocalDate.now();
    }
    
    public Booking(Room room, Guest guest) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required for booking.");
        }
        if (guest == null) {
            throw new IllegalArgumentException("Guest is required for booking.");
        }
        this.room = room;
        this.guest = guest;
        this.checkInDate = LocalDate.now();
    }
   
    public LocalDate getCheckInDate() {
        return checkInDate != null ? checkInDate : LocalDate.now();
    }

    public Room getRoom() {
        return room;
    }

    public Guest getGuest() {
        return guest;
    }

    // Retained for backwards compatibility
    public Guest getGuests() {
        return guest;
    }

    public void setRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        this.room = room;
    }

    public void setGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null.");
        }
        this.guest = guest;
    }

    // Retained for backwards compatibility
    public void setGuests(Guest guest) {
        setGuest(guest);
    } 
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking Date : ").append(checkInDate != null ? checkInDate : "N/A");
        sb.append("\nGuest Name : ").append(guest != null ? guest.getName() : "N/A");
        sb.append("\nGuest ID : ").append(guest != null ? guest.getGuestID() : "N/A");
        sb.append("\nRoom Number : ").append(room != null ? room.getRoomNumber() : "N/A");
        sb.append("\n===========\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Booking)) return false;
        Booking b = (Booking) obj;
        return Objects.equals(room, b.room) && Objects.equals(guest, b.guest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, guest);
    }
}
