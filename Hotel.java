
import java.util.*;

public class Hotel {
    private Manager manager;
    private Receptionist receptionist;

    public Hotel(Manager manager, Receptionist receptionist) {
        this.manager = manager;
        this.receptionist = receptionist;
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

        Input.sc.nextLine(); // consumes new line from the main menu selection
    
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
            } else {
                tries--;
                if (tries > 0) {
                    System.out.println("Incorrect Passcode! " + tries + " attempt(s) remaining.");
                }
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
            } else {
                tries--;
                if (tries > 0) {
                    System.out.println("Incorrect Passcode! " + tries + " attempt(s) remaining.");
                }
            }
        }
    
        System.out.println("Too many failed attempts...");
        return false;
    }

    public boolean isAvailable(ArrayList<Room> r) {
        for (Room room : r) {
            if (room.getStatus())  {
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
    
        System.out.print("Select Room No: ");
        int roomNo = Input.sc.nextInt();
        Input.sc.nextLine();
        
        Room room = manager.selectRoom(roomNo);
    
        if (room == null) {
            return; 
        }
    
        if (room.isAvailable()) {
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
        } else {
            System.out.println("Room " + roomNo + " is already booked!");
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

    int roomNum = receptionist.checkOut(id); // this returns -1 if checkout fails....
    if (roomNum != -1) {
        manager.setRoomStatus(roomNum, true); 
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
        int roomNo = -1;
        while (true) {
            System.out.print("Enter Room Number: ");
            roomNo = Input.sc.nextInt();
            if (!manager.roomExists(roomNo)) {
                System.out.println("Room not Founds!\nTry a different room No.");
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
        System.out.println("Enter contact Number : ");
        int contact = Input.sc.nextInt();
        Input.sc.nextLine();
        System.out.println("Enter total family Members : ");
        int familyMember = Input.sc.nextInt();
        Input.sc.nextLine();

        Guest guest = new Guest(id, name, contact, familyMember);

        receptionist.modifyGuestInBooking(roomNo, guest); //calling the receptionist method

    }

    public void displayAvailableRooms() {
        manager.displayRoomAvailability();

    }

    public void displayAllBookings() {
        receptionist.displayBookingHistory();

    }


    // Manager methods
    public void addRoom() {
        RoomType rt = null;
        while (rt == null) {
            System.out.println("1.SINGLE-ROOM ");
            System.out.println("2.DOUBLE-ROOM ");
            System.out.println("3.FAMILY-ROOM ");
            System.out.print("Enter Room Type : ");
            int n = Input.sc.nextInt();
            switch (n) {
                case 1: rt = RoomType.SINGLEROOM; break;
                case 2: rt = RoomType.DOUBLEROOM; break;
                case 3: rt = RoomType.FAMILYROOM; break;
                default: System.out.println("Invalid Input. TryAgain!");
                    break;
            }

        }
        int roomNo;
        while (true) {
            System.out.println("Enter Room Data...");
            System.out.print("Enter Room No : ");
            roomNo = Input.sc.nextInt();
            if (manager.roomExists(roomNo)) {
                System.out.println("Room " + roomNo + "Already Exists!\nTry Entring a new Room No : ");
                continue;
            }
            break;
        }  
        System.out.print("Enter Price per Night : ");
        double ppn = Input.sc.nextDouble();
        manager.addRoom(roomNo, ppn, rt);
        

    }

    public void removeRoom() {
        System.out.print("Enter Room Number : ");
        int n = Input.sc.nextInt();
        manager.removeRoom(n); 

    }

    public void searchRoom() {
        System.out.println("Enter Room Number : ");
        int roomNumber = Input.sc.nextInt();

        manager.searchRoom(roomNumber);
   

    }

    public void displayRooms() {
        System.out.println("Rooms And Their Status!");
        manager.displayAllRooms();
    }

    public void addStaff() {
        Input.sc.nextLine(); 
        Staff staff;
        String id;
        System.out.println("Enter Staff Details: ");
        while (true) { 
            System.out.print("ID: ");
            id = Input.sc.nextLine();
            if (manager.staffExists(id)) {
                System.out.print("Staff with the given ID already exists.\nTry Again with a new ID: ");
                continue;
            }
            break;
            
        }
        System.out.print("Name: ");
        String name = Input.sc.nextLine();
        System.out.print("Contact: ");
        int ct = Input.sc.nextInt();
        System.out.print("Salary: ");
        double sal = Input.sc.nextDouble();
        Input.sc.nextLine(); //consumes the newline character from input buffer
        System.out.println("Description: ");
        String desc = Input.sc.nextLine();
        System.out.println("Shift [DAY / NIGHT / ALL-DAY]");
        String sh;
        Staff.Shift shift = null;
        while (shift == null) { 
            try {
                sh = Input.sc.nextLine().toUpperCase();
                shift = Staff.Shift.valueOf(sh); //enum used!!!!! .valueOf(sh) converts the string inside into enum and if it does not matches throws illegalArgument exception;
                break;
            } catch (IllegalArgumentException ie) {
                System.out.println("Shift can only be [DAY / NIGHT / ALL-DAY].");
            }
        }
        staff = new Staff(name, ct, sal, id, desc, shift);
        manager.addStaff(staff);
    }

    public void removeStaff() {
        Input.sc.nextLine(); 
        System.out.println("Removing Staff...");
        System.out.print("Enter Staff ID: ");
        String id = Input.sc.nextLine();
        System.out.print("Enter Name: ");
        String name = Input.sc.nextLine();
        manager.removeStaff(name, id);
        
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
        boolean exit = false;

        while(!exit) {
            displayMainMenu();
            int choice = Input.sc.nextInt();

            switch (choice) {
                case 1:
                    // Manager Module
                    if (managerLogin()) {
                        boolean logout = false;
                        manager.load();
                        while(!logout) {
                            displayManagerMenu();
                            int mChoice = Input.sc.nextInt();

                            switch (mChoice) {
                                case 1: addRoom(); break;
                                case 2:removeRoom();break;
                                case 3:searchRoom();break;
                                case 4:displayRooms();break;
                                case 5:addStaff();break;
                                case 6:removeStaff();break;
                                case 7:searchStaff();break;
                                case 8:logout = true;
                                    Input.sc.nextLine();
                                    manager.save();
                                    System.out.println("Exiting the Manager module...\n");    
                                    break;
                                default:
                                    System.out.println("Invalid choice. Try Again...");
                                    break;
                            }
                        } 
                }
                    break;

                
                // Receptionist module
                case 2:
                    if(receptionistLogin()) {
                        boolean log = false;
                        receptionist.load();
                        while (!log) {
                            displayReceptionistMenu();
                            int rChoice = Input.sc.nextInt();
                            switch (rChoice) {
                                case 1:checkInGuest();break;
                                case 2:checkOutGuest();break;
                                case 3:searchBooking();break;
                                case 4:searchGuest();break;
                                case 5:modifyBooking();break;
                                case 6:displayAvailableRooms();break;
                                case 7:displayAllBookings();break;
                                case 8:log = true;
                                    receptionist.save();
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
                    System.out.println("Exiting the System...\n");
                    return;
                default:
                    break;
            }
        }
    }
    
    public enum RoomType { //this concept is used to make room creation of any specific type in a way that deals less with strings upper case lowercase and unwanted prompts.
        SINGLEROOM,
        DOUBLEROOM,
        FAMILYROOM
    
    }
}


