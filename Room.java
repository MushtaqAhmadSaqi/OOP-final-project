import java.io.*;
public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private double pricePerNight;
    private boolean status;

    public Room(int roomNumber, double pricePerNight) { 
        setRoomNumber(roomNumber);
        setPricePerNight(pricePerNight);
        this.status = true;
    }

    public abstract boolean isAvailable();
    public abstract double calculatePrice(int days);
    public abstract int getMaxCapacity();

    public int getRoomNumber() {
        return roomNumber;
    }
    
    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean getStatus() {
        return status;
    }

    public String AvailableOrBooked() {
        return this.getStatus() ? "AVAILABLE" : "BOOKED";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return this.roomNumber == room.roomNumber;
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode() + Integer.hashCode(roomNumber);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=====  Room Details  ==== \n");
        sb.append("Room Number : ").append(roomNumber);
        sb.append("\nPrice Per Night : ").append(pricePerNight);
        sb.append("\nStatus : ");
        sb.append(status ? "Available" : "Booked");
        return sb.toString();
    }

    public void setRoomNumber(int roomNumber) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Invalid room number.");
        }
        this.roomNumber = roomNumber;
    }    

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.pricePerNight = pricePerNight;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

class SingleRoom extends Room {
    private static final long serialVersionUID = 1L;

    private final String bedType = "Single";
    private final int maxCapacity = 1;

    public SingleRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, pricePerNight);
    }

    @Override
    public boolean isAvailable() {
        return getStatus();  
    }

    @Override
    public double calculatePrice(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be at least 1.");
        }
        return days * getPricePerNight();
    }

    public String getBedType() {
        return bedType;
    }

    @Override
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSingleRoom\n");
        sb.append("bedType : ").append(bedType);
        sb.append("\nMax Capacity : ").append(maxCapacity);
        return super.toString() + sb.toString();
    }
}

class DoubleRoom extends Room {
    private static final long serialVersionUID = 1L;

    private final String bedType = "Queen";
    private final int maxCapacity = 2;

    public DoubleRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, pricePerNight);
    }

    @Override
    public boolean isAvailable() {
        return getStatus();  
    }

    @Override
    public double calculatePrice(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be at least 1.");
        }
        return days * getPricePerNight();
    }

    public String getBedType() {
        return bedType;
    }

    @Override
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nDoubleRoom\n");
        sb.append("bedType : ").append(bedType);
        sb.append("\nMax Capacity : ").append(maxCapacity);
        return super.toString() + sb.toString();
    }
}

class FamilyRoom extends Room {
    private static final long serialVersionUID = 1L;

    private final String bedType = "Two Double Beds";
    private final int maxCapacity = 4;

    public FamilyRoom(int roomNumber, double pricePerNight) {
        super(roomNumber, pricePerNight);
    }

    @Override
    public boolean isAvailable() {
        return getStatus();  
    }

    @Override
    public double calculatePrice(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be at least 1.");
        }
        return days * getPricePerNight();
    }

    public String getBedType() {
        return bedType;
    }

    @Override
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nFamilyRoom\n");
        sb.append("bedType : ").append(bedType);
        sb.append("\nMax Capacity : ").append(maxCapacity);
        return super.toString() + sb.toString();
    }
}
