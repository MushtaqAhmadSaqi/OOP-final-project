import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class Hotel {
    private Manager manager;
    private Receptionist receptionist;

    public Hotel(Manager manager, Receptionist receptionist) {
        this.manager = manager;
        this.receptionist = receptionist;
        synchronizeRoomStatus();
    }

    private void saveAll() {
        if (manager != null) manager.save();
        if (receptionist != null) receptionist.save();
    }

    private void loadAll() {
        if (manager != null) manager.load();
        if (receptionist != null) receptionist.load();
        synchronizeRoomStatus();
    }

    private void synchronizeRoomStatus() {
        if (manager == null || receptionist == null || manager.getRooms() == null || receptionist.getBookings() == null) {
            return;
        }

        // Reset all rooms to available
        for (Room room : manager.getRooms()) {
            room.setStatus(true);
        }

        // Mark booked rooms as unavailable and sync booking's room reference
        for (Booking booking : receptionist.getBookings()) {
            if (booking.getRoom() != null) {
                Room masterRoom = manager.selectRoom(booking.getRoom().getRoomNumber());
                if (masterRoom != null) {
                    masterRoom.setStatus(false);
                    booking.setRoom(masterRoom);
                }
            }
        }
    }

    private static boolean constantTimePasswordCheck(String input, String expected) {
        if (input == null || expected == null) return false;
        byte[] a = input.getBytes(StandardCharsets.UTF_8);
        byte[] b = expected.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }

    public void displayMainMenu() {
        System.out.println("\n====== Hotel Management System ======");
        System.out.println("1. Login as Manager");
        System.out.println("2. Login as Receptionist");
        System.out.println("3. Exit");
    }

    public void displayReceptionistMenu() {
        System.out.println("\n====== Receptionist Module ======");
        System.out.println("1. Check-in a new guest");
        System.out.println("2. Check-out guest");
        System.out.println("3. Search booking");
        System.out.println("4. Search Guest");
        System.out.println("5. Modify a Booking");
        System.out.println("6. Display Available rooms");
        System.out.println("7. Display all Bookings");
        System.out.println("8. Log out");
    }

    public void displayManagerMenu() {
        System.out.println("\n====== Manager Module ======");
        System.out.println("1. Add new room");
        System.out.println("2. Remove room");
        System.out.println("3. Search Room");
        System.out.println("4. Display all rooms");
        System.out.println("5. Add Staff Member");
        System.out.println("6. Remove Staff Member");
        System.out.println("7. Search Staff Member");
        System.out.println("8. Log out");
    }

    public boolean managerLogin() {
        System.out.println("\n====== Manager Login Portal ======");
        String inputID = Input.readLine("Enter Manager ID: ");

        if (!inputID.equalsIgnoreCase(manager.getManagerID())) {
            System.out.println("Manager ID not found.");
            System.out.println("Returning to Main Menu...");
            return false;
        }

        int tries = 3;
        while (tries > 0) {
            String inputPass = Input.readLine("Enter Passcode: ");

            if (constantTimePasswordCheck(inputPass, manager.getPassCode())) {
                System.out.println("\nLogin Successful! Welcome back, " + manager.getName() + ".");
                return true;
            }

            tries--;
            if (tries > 0) {
                System.out.println("Incorrect Passcode! " + tries + " attempt(s) remaining.");
            }
        }

        System.out.println("Too many failed attempts. Returning to Main Menu...");
        return false;
    }

    public boolean receptionistLogin() {
        System.out.println("\n====== Receptionist Login Portal ======");
        String inputID = Input.readLine("Enter Receptionist ID: ");

        if (!inputID.equalsIgnoreCase(receptionist.getReceptID())) {
            System.out.println("Receptionist not found.");
            System.out.println("Returning to Main Menu...");
            return false;
        }

        int tries = 3;
        while (tries > 0) {
            String inputPass = Input.readLine("Enter Passcode: ");

            if (constantTimePasswordCheck(inputPass, receptionist.getPassCode())) {
                System.out.println("\nLogin Successful! Welcome back, " + receptionist.getName() + ".");
                return true;
            }

            tries--;
            if (tries > 0) {
                System.out.println("Incorrect Passcode! " + tries + " attempt(s) remaining.");
            }
        }

        System.out.println("Too many failed attempts. Returning to Main Menu...");
        return false;
    }

    public boolean isAvailable(ArrayList<Room> rooms) {
        if (rooms != null) {
            for (Room room : rooms) {
                if (room.getStatus()) {
                    return true;
                }
            }
        }
        System.out.println("No Rooms Currently Available!");
        return false;
    }

    public void checkInGuest() {
        System.out.println("\n====== Guest Check-In ======\n");

        if (!manager.displayRoomAvailability()) {
            return;
        }

        try {
            int roomNo = Input.readPositiveInt("Select Room No (or 0 to cancel): ");
            if (roomNo == 0) return;

            Room room = manager.selectRoom(roomNo);
            if (room == null) {
                System.out.println("Room " + roomNo + " does not exist.");
                return;
            }
            if (!room.isAvailable()) {
                System.out.println("Room " + roomNo + " is already booked.");
                return;
            }

            System.out.println("\n--- Guest Details ---");
            String id = Input.readNonEmptyString("Enter guest ID: ");
            String name = Input.readNonEmptyString("Enter guest name: ");
            int contact = Input.readPositiveInt("Enter contact number (numeric): ");
            int familyMember = Input.readInt("Enter total family members (max " + room.getMaxCapacity() + "): ", 1, room.getMaxCapacity());

            Guest guest = new Guest(id, name, contact, familyMember);
            receptionist.checkIn(room, guest);
            saveAll();
        } catch (IllegalArgumentException ex) {
            System.out.println("Check-in failed: " + ex.getMessage());
        }
    }

    public void checkOutGuest() {
        System.out.println("\n====== Guest Check-Out ======");
        System.out.println("=== Currently Active Bookings ===");
        receptionist.displayActiveGuestIDs();
        System.out.println("---------------------------------");

        String id = Input.readLine("Enter Guest ID to check out (leave blank to cancel): ");
        if (id.isEmpty()) return;

        int roomNum = receptionist.checkOut(id);
        if (roomNum != -1) {
            manager.setRoomStatus(roomNum, true);
            saveAll();
        }
    }

    public void searchBooking() {
        System.out.println("\n=== Booking Search ===\n");
        String query = Input.readLine("Enter Guest Name or ID: ");
        receptionist.searchBooking(query);
    }

    public void searchGuest() {
        System.out.println("\n=== Guest Search ===\n");
        receptionist.displayActiveGuestIDs();
        String id = Input.readLine("\nEnter Guest ID: ");
        receptionist.displayGuestHistory(id);
    }

    public void modifyBooking() {
        System.out.println("\n=== Modifying Booking ===\n");
        if (receptionist.getBookings().isEmpty()) {
            System.out.println("No active bookings to modify.");
            return;
        }

        receptionist.displayBookingHistory();
        int roomNo = Input.readPositiveInt("Enter Room Number to modify (or 0 to cancel): ");
        if (roomNo == 0) return;

        // Verify booking exists before prompting for details
        boolean hasBooking = false;
        for (Booking b : receptionist.getBookings()) {
            if (b.getRoom() != null && b.getRoom().getRoomNumber() == roomNo) {
                hasBooking = true;
                break;
            }
        }

        if (!hasBooking) {
            System.out.println("No active booking found for Room " + roomNo + ".");
            return;
        }

        try {
            System.out.println("--- Enter New Guest Details ---");
            String id = Input.readNonEmptyString("Enter new guest ID: ");
            String name = Input.readNonEmptyString("Enter new guest name: ");
            int contact = Input.readPositiveInt("Enter new contact number: ");
            int familyMember = Input.readPositiveInt("Enter total family members: ");

            Guest guest = new Guest(id, name, contact, familyMember);
            if (receptionist.modifyGuestInBooking(roomNo, guest)) {
                saveAll();
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Modification failed: " + ex.getMessage());
        }
    }

    public void displayAvailableRooms() {
        manager.displayRoomAvailability();
    }

    public void displayAllBookings() {
        receptionist.displayBookingHistory();
    }

    public void addRoom() {
        System.out.println("\n--- Add New Room ---");
        System.out.println("1. SINGLE-ROOM");
        System.out.println("2. DOUBLE-ROOM");
        System.out.println("3. FAMILY-ROOM");
        int n = Input.readInt("Enter Room Type (1-3): ", 1, 3);
        RoomType rt;
        switch (n) {
            case 1: rt = RoomType.SINGLEROOM; break;
            case 2: rt = RoomType.DOUBLEROOM; break;
            case 3: rt = RoomType.FAMILYROOM; break;
            default: return;
        }

        try {
            int roomNo = Input.readPositiveInt("Enter Room Number: ");
            double ppn = Input.readDouble("Enter Price per Night: ", 1.0);
            manager.addRoom(roomNo, ppn, rt);
            saveAll();
        } catch (IllegalArgumentException ex) {
            System.out.println("Room could not be added: " + ex.getMessage());
        }
    }

    public void removeRoom() {
        int n = Input.readPositiveInt("Enter Room Number to remove: ");
        if (manager.removeRoom(n)) {
            saveAll();
        }
    }

    public void searchRoom() {
        int roomNumber = Input.readPositiveInt("Enter Room Number: ");
        manager.searchRoom(roomNumber);
    }

    public void displayRooms() {
        System.out.println("Rooms And Their Status:");
        manager.displayAllRooms();
    }

    public void addStaff() {
        try {
            System.out.println("\n--- Enter Staff Details ---");
            String id = Input.readNonEmptyString("Staff ID: ");
            String name = Input.readNonEmptyString("Name: ");
            int ct = Input.readPositiveInt("Contact Number: ");
            double sal = Input.readDouble("Salary: ", 0.0);
            String desc = Input.readNonEmptyString("Role Description: ");

            Staff.Shift shift = null;
            while (shift == null) {
                System.out.println("Shift Options: 1. DAY  2. NIGHT  3. ALLDAY");
                int choice = Input.readInt("Select shift (1-3): ", 1, 3);
                switch (choice) {
                    case 1: shift = Staff.Shift.DAY; break;
                    case 2: shift = Staff.Shift.NIGHT; break;
                    case 3: shift = Staff.Shift.ALLDAY; break;
                }
            }

            Staff staff = new Staff(name, ct, sal, id, desc, shift);
            manager.addStaff(staff);
            saveAll();
        } catch (IllegalArgumentException ex) {
            System.out.println("Staff could not be added: " + ex.getMessage());
        }
    }

    public void removeStaff() {
        System.out.println("\n--- Removing Staff ---");
        String id = Input.readNonEmptyString("Enter Staff ID: ");
        String name = Input.readLine("Enter Name (optional, press Enter to skip): ");
        if (manager.removeStaff(name, id)) {
            saveAll();
        }
    }

    public void searchStaff() {
        System.out.println("\n--- Searching Staff ---");
        String id = Input.readLine("Enter Staff ID (optional): ");
        String name = Input.readLine("Enter Name (optional): ");
        manager.searchStaff(name, id);
    }

    public void run() {
        while (true) {
            displayMainMenu();
            int choice = Input.readInt("Enter your choice (1-3): ", 1, 3);

            switch (choice) {
                case 1:
                    if (managerLogin()) {
                        loadAll();
                        boolean logout = false;
                        while (!logout) {
                            displayManagerMenu();
                            int mChoice = Input.readInt("Enter your choice (1-8): ", 1, 8);

                            switch (mChoice) {
                                case 1: addRoom(); break;
                                case 2: removeRoom(); break;
                                case 3: searchRoom(); break;
                                case 4: displayRooms(); break;
                                case 5: addStaff(); break;
                                case 6: removeStaff(); break;
                                case 7: searchStaff(); break;
                                case 8:
                                    logout = true;
                                    saveAll();
                                    System.out.println("Exiting the Manager module...\n");
                                    break;
                                default:
                                    System.out.println("Invalid choice.");
                                    break;
                            }
                        }
                    }
                    break;

                case 2:
                    if (receptionistLogin()) {
                        loadAll();
                        boolean logout = false;
                        while (!logout) {
                            displayReceptionistMenu();
                            int rChoice = Input.readInt("Enter your choice (1-8): ", 1, 8);
                            switch (rChoice) {
                                case 1: checkInGuest(); break;
                                case 2: checkOutGuest(); break;
                                case 3: searchBooking(); break;
                                case 4: searchGuest(); break;
                                case 5: modifyBooking(); break;
                                case 6: displayAvailableRooms(); break;
                                case 7: displayAllBookings(); break;
                                case 8:
                                    logout = true;
                                    saveAll();
                                    System.out.println("Exiting the Receptionist module...\n");
                                    break;
                                default:
                                    System.out.println("Invalid choice.");
                                    break;
                            }
                        }
                    }
                    break;

                case 3:
                    saveAll();
                    System.out.println("Exiting the System. Goodbye!\n");
                    return;

                default:
                    System.out.println("Invalid choice. Try Again...");
                    break;
            }
        }
    }
    
    public enum RoomType {
        SINGLEROOM,
        DOUBLEROOM,
        FAMILYROOM
    }
}
