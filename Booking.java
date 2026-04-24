import java.io.*;
import java.time.LocalDate;

public class Booking implements Serializable {

    private Room room;
    private Guest guest;
    private LocalDate checkInDate;
    
    public Booking() {
        
    }
    
    public Booking(Room room, Guest guests) {
        this.room = room;
        this.guest = guests;  
        checkInDate = LocalDate.now();
    }
   
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public Room getRoom() {
        return room;
    }

    public Guest getGuests() {
        return guest;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setGuests(Guest guests) {
        this.guest = guests;
    } 
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking Date : ").append(checkInDate);
        sb.append("\nGuest Name : ").append(guest.getName());
        sb.append("\nRoom Number : ").append(room.getRoomNumber());
        sb.append("\n===========\n");
        return sb.toString();

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking b = (Booking) obj;
        return room.equals(b.room) && guest.equals(b.guest);
    }

}
