public class Booking {
    private int roomNumber;
    Guest guests;
    
    public Booking() {
    }

    public Booking(int roomNumber, Guest guests) {
        this.roomNumber = roomNumber;
        this.guests = guests;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public Guest getGuests() {
        return guests;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setGuests(Guest guests) {
        this.guests = guests;
    } 

    
}
