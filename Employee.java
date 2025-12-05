import java.time.LocalDate;
import java.util.*;
public class Employee extends Person{
    private String uniqueID;
    private double salary;
    private LocalDate dateJoin;
    Scanner sc = new Scanner(System.in);
    
    
    public Employee(String name, int contactNumber) {
        super(name,contactNumber);
    }

    public Employee(String name, String address, int contactNumber, String uniqueID, double salary,
            LocalDate dateJoin) {
        super(name, address, contactNumber);
        this.uniqueID = uniqueID;
        this.salary = salary;
        this.dateJoin = dateJoin;
    }

    public boolean equals(Person p){
        if(this.getName().equalsIgnoreCase(p.getName())){
            System.out.println("This is the same Person");
            return true;
        }
        return false;
    }

    public double calculateSalary(){
        // System.out.println("Enter the number of day ");
        return salary;
    }
    
    public String toString(){
        return "Employee Name : " + getName() + "Employee ID : " + getUniqueID() + " And Salary is : " + calculateSalary() ;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getDateJoin() {
        return dateJoin;
    }   
}

class Staff extends Employee{
    private String staffID;
    private String staffDescription;
    private String staffType;

    public Staff(String name, int contactNumber,
            String staffID, String staffDescription, String staffType) {
        super(name, contactNumber);
        this.staffID = staffID;
        this.staffDescription = staffDescription;
        this.staffType = staffType;
    }
}

class Manager extends Employee{
    private String managerID;
    private Employee employees;
    private ArrayList<Room> rooms;
    private ArrayList<Staff> staff;

    public Manager(String name, int contactNumber, String managerID) {
        super(name, contactNumber);
        this.managerID = managerID;
    }

    public void addRoom(Room add){
        rooms.add(add);
    }

    public void removeRoom(int roomNumber , boolean status){
        rooms.removeIf((r) -> 
                r.getRoomNumber() == roomNumber & r.getStatus() == status);
        
    }

    public void addEmployee(Employee emp){
        // employees.
    }
}

class Receptionist extends Employee{
    private String receptID;
    Room rooms;

    
    public Receptionist(String name, int contactNumber, String receptID) {
        super(name, contactNumber);
        this.receptID = receptID;
    }


    ArrayList<Booking> bookings;

    public void checkIn(Booking guest){
        bookings.add(guest);
    }

    public void checkOut(Booking guest){
        bookings.remove(guest);
    }

    public void modifyGuestInBooking(int roomNumber, Guest newGuest) {
    for (Booking b : bookings) {
        if (b.getRoomNumber() == roomNumber) {
            b.setGuests(newGuest); 
            System.out.println("Guest in room " + roomNumber + " updated successfully.");
            return;
        }
    }
        System.out.println("Booking for room " + roomNumber + " not found.");
    }

    public void searchRoom(int searchRoom){
        if(rooms.getRoomNumber() == searchRoom){
            System.out.println("Room with Room Number " + searchRoom + " is found");
        } else {
         System.out.println("Room with Room Number " + searchRoom + " is not found! ");
        }
    }
}
    
    
