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
        manager.save();
        receptionist.save();
    }

    private void loadAll() {
        manager.load();
        receptionist.load();
        synchronizeRoomStatus();
    }

    private void synchronizeRoomStatus() {
        if (manager == null || receptionist == null || manager.getRooms() == null || receptionist.getBookings() == null) {
            return;
        }

        for (Room room : manager.getRooms()) {
            room.setStatus(true);
        }

        for (Booking booking : receptionist.getBookings()) {
            Room masterRoom = manager.selectRoom(booking.getRoom().getRoomNumber());
            if (masterRoom != null) {
                masterRoom.setStatus(false);
                booking.setRoom(masterRoom);
            }
        }
    }

    public void displayMainMenu() {
        System.out.println("\n====== Hotel Management System ======");
        System.out.println("1. Login as Manager");
        System.out.println("2. Login as Receptionist");
        System.out.println("3. Exit");
        System.out.print("Enter your choice : ");
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
        System.out.print("Enter your choice : ");
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
        System.out.print("Enter your choice : ");
    }

    public boolean managerLogin() {
        Input.sc.nextLine();

        System.out.println("\n====== Manager Login Portal ======");
        System.out.print("Enter Manager ID: ");
        String inputID = Input.sc.nextLine();

        if (!inputID.equals(manager.getManagerID())) {
            System.out.println("Manager ID not found.");
            System.out.println("Returning to Main Menu...");
            return false;
        }

        int tries = 3;
        while (tries > 0) {
            System.out.print("Enter Passcode: ");
            String inputPass = Input.sc.nextLine();

            if (inputPass.equals(manager.getPassCode())) {
                System.out.println("\nLogin Successful! Welcome back, " + manager.getName() + ".");
                return true;
            }

            tries--;
            if (tries > 0) {
                System.out.println("Incorrect Passcode! " + tries + " attempt(s) remaining.");
            }
        }

        System.out.println("Too many failed attempts...");
        return false;
    }

    public boolean receptionistLogin() {
        Input.sc.nextLine();

        System.out.println("\n====== Receptionist Login Portal ======");
        System.out.print("Enter Receptionist ID: ");
        String inputID = Input.sc.nextLine();

        if (!inputID.equals(receptionist.getReceptID())) {
            System.out.println("Receptionist not found.");
            System.out.println("Returning to Main Menu...");
            return false;
        }

        int tries = 3;
        while (tries > 0) {
            System.out.print("Enter Passcode: ");
            String inputPass = Input.sc.nextLine();

            if (inputPass.equals(receptionist.getPassCode())) {
                System.out.println("\nLogin Successful! Welcome back, " + receptionist.getName() + ".");
                return true;
            }

            tries--;
            if (tries > 0) {
                System.out.println("Incorrect Passcode! " + tries + " attempt(s) remaining.");
            }
        }

        System.out.println("Too many failed attempts...");
        return false;
    }

    public boolean isAvailable(ArrayList<Room> rooms) {
        for (Room room : rooms) {
            if (room.getStatus()) {
                return true;
            }
        }
        System.out.println("No Rooms Currently Available!");
        return false;
    }

    public void checkInGuest() {
        Input.sc.nextLine();

        System.out.println("\n====== Guest Check-In  ======\n");

        if (!manager.displayRoomAvailability()) {
            System.out.println("No Rooms Available!");
            return;
        }

        try {
            System.out.print("Select Room No: ");
            int roomNo = Input.sc.nextInt();
            Input.sc.nextLine();

            Room room = manager.selectRoom(roomNo);
            if (room == null) {
                return;
            }

            System.out.println("\n--- Guest Details ---");
            System.out.print("Enter guest ID : ");
            String id = Input.sc.nextLine();
            System.out.print("Enter name : ");
            String name = Input.sc.nextLine();
            System.out.print("Enter contact Number : ");
            int contact = Input.sc.nextInt();
            System.out.print("Enter total family Members : ");
            int familyMember = Input.sc.nextInt();
            Input.sc.nextLine();

            Guest guest = new Guest(id, name, contact, familyMember);
            receptionist.checkIn(room, guest);
            saveAll();
        } catch (InputMismatchException ex) {
            Input.sc.nextLine();
            System.out.println("Invalid input. Numeric fields must contain numbers.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Check-in failed: " + ex.getMessage());
        }
    }

    public void checkOutGuest() {
        Input.sc.nextLine();

        System.out.println("\n====== Guest Check-Out  ======");
        System.out.println("=== Currently Active Bookings ===");
        receptionist.displayActiveGuestIDs();
        System.out.println("---------------------------------");

        System.out.print("Enter Guest ID to check out: ");
        String id = Input.sc.nextLine();

        int roomNum = receptionist.checkOut(id);
        if (roomNum != -1) {
            manager.setRoomStatus(roomNum, true);
            saveAll();
        }
    }

    public void searchBooking() {
        Input.sc.nextLine();

        System.out.println("\n=== Booking Search ===\n");
        System.out.print("Enter Guest Name : ");
        String name = Input.sc.nextLine();
        receptionist.searchBooking(name);
    }

    public void searchGuest() {
        Input.sc.nextLine();
        System.out.println("\n=== Guest Search ===\n");
        receptionist.displayActiveGuestIDs();
        System.out.print("\nEnter Guest ID: ");
        String id = Input.sc.nextLine();
        receptionist.displayGuestHistory(id);
    }

    public void modifyBooking() {
        System.out.println("\n=== Modifying Booking ===\n");
        try {
            int roomNo;
            while (true) {
                System.out.print("Enter Room Number: ");
                roomNo = Input.sc.nextInt();
                if (!manager.roomExists(roomNo)) {
                    System.out.println("Room not Found!\nTry a different room No.");
                    continue;
                }
                Input.sc.nextLine();
                break;
            }

            System.out.println("--- Enter New Guest Details ---");
            System.out.print("Enter guest ID : ");
            String id = Input.sc.nextLine();
            System.out.print("Enter name : ");
            String name = Input.sc.nextLine();
            System.out.print("Enter contact Number : ");
            int contact = Input.sc.nextInt();
            Input.sc.nextLine();
            System.out.print("Enter total family Members : ");
            int familyMember = Input.sc.nextInt();
            Input.sc.nextLine();

            Guest guest = new Guest(id, name, contact, familyMember);
            receptionist.modifyGuestInBooking(roomNo, guest);
            saveAll();
        } catch (InputMismatchException ex) {
            Input.sc.nextLine();
            System.out.println("Invalid input. Numeric fields must contain numbers.");
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
        RoomType rt = null;
        while (rt == null) {
            System.out.println("1.SINGLE-ROOM ");
            System.out.println("2.DOUBLE-ROOM ");
            System.out.println("3.FAMILY-ROOM ");
            System.out.print("Enter Room Type : ");
            int n = Input.sc.nextInt();
            switch (n) {
                case 1:
                    rt = RoomType.SINGLEROOM;
                    break;
                case 2:
                    rt = RoomType.DOUBLEROOM;
                    break;
                case 3:
                    rt = RoomType.FAMILYROOM;
                    break;
                default:
                    System.out.println("Invalid Input. Try Again!");
                    break;
            }
        }

        try {
            System.out.println("Enter Room Data...");
            System.out.print("Enter Room No : ");
            int roomNo = Input.sc.nextInt();
            System.out.print("Enter Price per Night : ");
            double ppn = Input.sc.nextDouble();
            manager.addRoom(roomNo, ppn, rt);
            saveAll();
        } catch (InputMismatchException ex) {
            Input.sc.nextLine();
            System.out.println("Invalid input. Room number and price must be numeric.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Room could not be added: " + ex.getMessage());
        }
    }

    public void removeRoom() {
        try {
            System.out.print("Enter Room Number : ");
            int n = Input.sc.nextInt();
            manager.removeRoom(n);
            saveAll();
        } catch (InputMismatchException ex) {
            Input.sc.nextLine();
            System.out.println("Invalid room number.");
        }
    }

    public void searchRoom() {
        try {
            System.out.println("Enter Room Number : ");
            int roomNumber = Input.sc.nextInt();
            manager.searchRoom(roomNumber);
        } catch (InputMismatchException ex) {
            Input.sc.nextLine();
            System.out.println("Invalid room number.");
        }
    }

    public void displayRooms() {
        System.out.println("Rooms And Their Status!");
        manager.displayAllRooms();
    }

    public void addStaff() {
        Input.sc.nextLine();
        try {
            System.out.println("Enter Staff Details: ");
            System.out.print("ID: ");
            String id = Input.sc.nextLine();
            System.out.print("Name: ");
            String name = Input.sc.nextLine();
            System.out.print("Contact: ");
            int ct = Input.sc.nextInt();
            System.out.print("Salary: ");
            double sal = Input.sc.nextDouble();
            Input.sc.nextLine();
            System.out.print("Description: ");
            String desc = Input.sc.nextLine();
            System.out.println("Shift [DAY / NIGHT / ALLDAY]");

            Staff.Shift shift = null;
            while (shift == null) {
                try {
                    String sh = Input.sc.nextLine().trim().toUpperCase().replace("-", "");
                    shift = Staff.Shift.valueOf(sh);
                } catch (IllegalArgumentException ie) {
                    System.out.println("Shift can only be [DAY / NIGHT / ALLDAY].");
                }
            }

            Staff staff = new Staff(name, ct, sal, id, desc, shift);
            manager.addStaff(staff);
            saveAll();
        } catch (InputMismatchException ex) {
            Input.sc.nextLine();
            System.out.println("Invalid input. Contact and salary must be numeric.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Staff could not be added: " + ex.getMessage());
        }
    }

    public void removeStaff() {
        Input.sc.nextLine();
        System.out.println("Removing Staff...");
        System.out.print("Enter Staff ID: ");
        String id = Input.sc.nextLine();
        System.out.print("Enter Name: ");
        String name = Input.sc.nextLine();
        manager.removeStaff(name, id);
        saveAll();
    }

    public void searchStaff() {
        Input.sc.nextLine();
        System.out.println("Searching Staff...");
        System.out.print("Enter Staff ID: ");
        String id = Input.sc.nextLine();
        System.out.print("Enter Name: ");
        String name = Input.sc.nextLine();
        manager.searchStaff(name, id);
    }

    public void run() {
        while (true) {
            displayMainMenu();
            int choice = Input.sc.nextInt();

            switch (choice) {
                case 1:
                    if (managerLogin()) {
                        loadAll();
                        boolean logout = false;
                        while (!logout) {
                            displayManagerMenu();
                            int mChoice = Input.sc.nextInt();

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
                                    Input.sc.nextLine();
                                    saveAll();
                                    System.out.println("Exiting the Manager module...\n");
                                    break;
                                default:
                                    System.out.println("Invalid choice. Try Again...");
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
                            int rChoice = Input.sc.nextInt();
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
                                    System.out.println("Invalid choice. Enter Again...");
                                    break;
                            }
                        }
                    }
                    break;

                case 3:
                    saveAll();
                    System.out.println("Exiting the System...\n");
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
