

public abstract  class Room {
    private int roomNumber;
    private double pricePerNight;
    private boolean status;

    public Room(int roomNumber, double pricePerNight, boolean status) {
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public abstract boolean isAvaiable();
    // public abstract double calculatePrice();

    public int getRoomNumber() {
        return roomNumber;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean getStatus() {
        return status;
    }
}

interface Billable{
    double calculatePrice();
}
class Single extends Room implements Billable{
    private final String bedType = "Twin";
    private final int maxCapacity = 1;
    private final double price = 2000;

    public Single(int roomNumber, double pricePerNight, boolean status) {
        super(roomNumber, pricePerNight, status);
    }

    public boolean isAvaiable(){
        return getStatus();  
    }

    public double calculatePrice(){
       return price * getPricePerNight();
    }

    public String getBedType() {
        return bedType;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

}

class Double extends Room implements Billable{
    private final String bedType = "Queen";
    private final int maxCapacity = 2;
    private final double price = 4000;

    public Double(int roomNumber, double pricePerNight, boolean status) {
        super(roomNumber, pricePerNight, status);
    }

    public boolean isAvaiable(){
        return getStatus();  
    }

    public double calculatePrice(){
       return price * getPricePerNight();
    }

    public String getBedType() {
        return bedType;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}
class Family extends Room implements Billable{
    private final String bedType = "Bunk";
    private final int maxCapacity = 4;
    private final double price = 5500;

    public Family(int roomNumber, double pricePerNight, boolean status) {
        super(roomNumber, pricePerNight, status);
    }

    public boolean isAvaiable(){
        return getStatus();  
    }

    public double calculatePrice(){
       return price * getPricePerNight();
    }

    public String getBedType() {
        return bedType;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}