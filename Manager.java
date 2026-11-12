import java.util.*;

public class Manager extends Employee {
    private static final long serialVersionUID = 1L;

    private final String managerID;
    private final String passCode;
    private ArrayList<Room> rooms;
    private ArrayList<Staff> staff;

    public Manager(String name, int contactNumber, double salary, String managerID, String passCode) {
        super(name, contactNumber, salary);
        if (managerID == null || managerID.trim().isEmpty()) {
            throw new IllegalArgumentException("Manager ID cannot be empty.");
        }
        if (passCode == null || passCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Manager passcode cannot be empty.");
        }
        this.managerID = managerID.trim();
        this.passCode = passCode;
        this.rooms = FileManager.loadFromFile("rooms.ser");
        this.staff = FileManager.loadFromFile("staff.ser");
        if (this.rooms == null) this.rooms = new ArrayList<>();
        if (this.staff == null) this.staff = new ArrayList<>();
    }

    public Room selectRoom(int roomNo) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNo) {
                return room;
            }
        }
        return null;
    }

    public void addRoom(int roomNo, double pricePerNight, Hotel.RoomType rt) {
        if (roomNo <= 0) {
            throw new IllegalArgumentException("Room number must be greater than zero.");
        }
        if (pricePerNight <= 0 || Double.isNaN(pricePerNight) || Double.isInfinite(pricePerNight)) {
            throw new IllegalArgumentException("Price per night must be a valid positive number.");
        }
        if (rt == null) {
            throw new IllegalArgumentException("Room type is required.");
        }
        if (roomExists(roomNo)) {
            throw new IllegalArgumentException("Room " + roomNo + " already exists.");
        }

        Room room;
        switch (rt) {
            case SINGLEROOM:
                room = new SingleRoom(roomNo, pricePerNight);
                break;
            case DOUBLEROOM:
                room = new DoubleRoom(roomNo, pricePerNight);
                break;
            case FAMILYROOM:
                room = new FamilyRoom(roomNo, pricePerNight);
                break;
            default:
                throw new IllegalArgumentException("Room type invalid!");
        }
        rooms.add(room);
        System.out.println("ROOM\n" + room + "\nSUCCESSFULLY ADDED!");
    }

    public boolean removeRoom(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getRoomNumber() == roomNumber) {
                if (!room.getStatus()) {
                    System.out.println("Room " + roomNumber + " cannot be removed because it is currently booked!");
                    return false;
                }
                rooms.remove(i);
                System.out.println("Room " + roomNumber + " removed successfully.");
                return true;
            }
        }

        System.out.println("Error Removing Room: Room " + roomNumber + " not found.");
        return false;
    }

    public void searchRoom(int roomNumber) {
        Room r = selectRoom(roomNumber);
        if (r != null) {
            System.out.println("Room with Room Number " + roomNumber + " is found:");
            System.out.println(r);
        } else {
            System.out.println("Error Finding Room. Room " + roomNumber + " does not exist!");
        }
    }

    public void displayAllRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No Rooms to Display...");
            return;
        }
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    public ArrayList<Room> getRooms() {
        if (rooms == null) rooms = new ArrayList<>();
        return rooms;
    }

    public void addStaff(Staff s) {
        if (s == null) {
            throw new IllegalArgumentException("Staff member cannot be null.");
        }
        if (staffExists(s.getStaffID())) {
            throw new IllegalArgumentException("Staff with ID " + s.getStaffID() + " already exists.");
        }
        staff.add(s);
        System.out.println("Staff added successfully.");
    }

    public boolean staffExists(String staffID) {
        if (staffID == null || staffID.trim().isEmpty()) return false;
        String cleanID = staffID.trim();
        for (Staff s : staff) {
            if (s.getStaffID().equalsIgnoreCase(cleanID)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeStaff(String name, String staffID) {
        if (staffID == null || staffID.trim().isEmpty()) {
            System.out.println("Invalid staff ID.");
            return false;
        }
        String cleanId = staffID.trim();
        String cleanName = (name != null) ? name.trim() : "";

        for (int i = staff.size() - 1; i >= 0; i--) {
            Staff s = staff.get(i);
            if (s.getStaffID().equalsIgnoreCase(cleanId)) {
                if (!cleanName.isEmpty() && !s.getName().equalsIgnoreCase(cleanName)) {
                    System.out.println("Staff ID matches, but staff name does not match '" + cleanName + "'. Removal cancelled.");
                    return false;
                }
                staff.remove(i);
                System.out.println("Staff " + cleanId + " removed successfully.");
                return true;
            }
        }
        System.out.println("Staff with ID " + cleanId + " not found!");
        return false;
    }

    public void searchStaff(String name, String staffID) {
        String cleanName = (name != null) ? name.trim() : "";
        String cleanId = (staffID != null) ? staffID.trim() : "";

        if (cleanName.isEmpty() && cleanId.isEmpty()) {
            System.out.println("Please provide a staff name or ID to search.");
            return;
        }

        boolean found = false;
        for (Staff s : staff) {
            boolean nameMatch = !cleanName.isEmpty() && s.getName().equalsIgnoreCase(cleanName);
            boolean idMatch = !cleanId.isEmpty() && s.getStaffID().equalsIgnoreCase(cleanId);

            if (!cleanName.isEmpty() && !cleanId.isEmpty()) {
                if (nameMatch && idMatch) {
                    System.out.println("Staff Member FOUND (Name and ID match):");
                    System.out.println(s);
                    found = true;
                } else if (idMatch) {
                    System.out.println("Staff Member FOUND with ID " + cleanId + " (Name: " + s.getName() + "):");
                    System.out.println(s);
                    found = true;
                }
            } else if (nameMatch || idMatch) {
                System.out.println("Staff Member FOUND:");
                System.out.println(s);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Staff NOT found!");
        }
    }

    public boolean displayRoomAvailability() {
        boolean isAvailable = false;
        System.out.print("---- Currently Available Rooms ----\n");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.println(room);
                System.out.println();
                isAvailable = true;
            }
        }
        if (!isAvailable) {
            System.out.println("No rooms currently available.");
        }
        return isAvailable;
    }

    public boolean roomExists(int roomNo) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNo) {
                return true;
            }
        }
        return false;
    }

    public void setRoomStatus(int roomNumber, boolean isAvailable) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                r.setStatus(isAvailable);
                return;
            }
        }
        System.out.println("Error: Room " + roomNumber + " not found in Manager's Master List.");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Manager)) return false;
        Manager m = (Manager) obj;
        return managerID != null && managerID.equalsIgnoreCase(m.managerID);
    }

    @Override
    public int hashCode() {
        return managerID != null ? managerID.toLowerCase().hashCode() : 0;
    }

    public String getManagerID() {
        return managerID;
    }

    public String getPassCode() {
        return passCode;
    }
    
    public void save() {
        FileManager.saveToFile("rooms.ser", rooms);
        FileManager.saveToFile("staff.ser", staff);
    }

    public void load() {
        rooms = FileManager.loadFromFile("rooms.ser");
        staff = FileManager.loadFromFile("staff.ser");
        if (rooms == null) rooms = new ArrayList<>();
        if (staff == null) staff = new ArrayList<>();
    }

    public ArrayList<Staff> getStaff() {
        if (staff == null) staff = new ArrayList<>();
        return staff;
    }
}
