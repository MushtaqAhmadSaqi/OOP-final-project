import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SystemTestSuite {
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("[FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void assertThrows(String testName, Class<? extends Throwable> expected, Runnable r) {
        try {
            r.run();
            System.err.println("[FAIL] " + testName + " (expected " + expected.getSimpleName() + " but nothing was thrown)");
            testsFailed++;
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                System.out.println("[PASS] " + testName + " (threw " + t.getClass().getSimpleName() + ": " + t.getMessage() + ")");
                testsPassed++;
            } else {
                System.err.println("[FAIL] " + testName + " (expected " + expected.getSimpleName() + " but got " + t.getClass().getSimpleName() + ")");
                testsFailed++;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" RUNNING HOTEL SYSTEM EXPERT SECURITY & LOGIC TESTS");
        System.out.println("==================================================");

        // Clean up test files if any exist
        cleanupTestFiles();

        testPersonAndValidation();
        testRoomAndSubclasses();
        testStaffAndManager();
        testReceptionistAndBookingWorkflow();
        testCybersecurityFileManager();

        cleanupTestFiles();

        System.out.println("==================================================");
        System.out.println("TEST RESULTS: " + testsPassed + " passed, " + testsFailed + " failed.");
        System.out.println("==================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void cleanupTestFiles() {
        for (String f : Arrays.asList("rooms.ser", "staff.ser", "bookings.ser", "test_rooms.ser", "test_data.ser", "rooms.ser.tmp", "test_rooms.ser.tmp")) {
            try {
                Files.deleteIfExists(Paths.get(f));
            } catch (Exception ignored) {}
        }
    }

    private static void testPersonAndValidation() {
        System.out.println("\n--- 1. Testing Person, Guest & Validation ---");

        assertThrows("Empty person name should throw IllegalArgumentException", IllegalArgumentException.class, () -> {
            new Guest("G1", "   ", 12345, 1);
        });

        assertThrows("Non-positive contact number should throw IllegalArgumentException", IllegalArgumentException.class, () -> {
            new Guest("G1", "Valid Name", 0, 1);
        });

        assertThrows("Negative contact number should throw IllegalArgumentException", IllegalArgumentException.class, () -> {
            new Guest("G1", "Valid Name", -10, 1);
        });

        assertThrows("Zero family members should throw IllegalArgumentException", IllegalArgumentException.class, () -> {
            new Guest("G1", "Valid Name", 12345, 0);
        });

        Guest g1 = new Guest("G101", "Alice", 555123, 2);
        Guest g2 = new Guest("g101", "Alice", 555123, 2);
        assertTrue("Guest equality should be case-insensitive for ID", g1.equals(g2));
        assertTrue("Guest hash code should match for case-insensitive ID", g1.hashCode() == g2.hashCode());
    }

    private static void testRoomAndSubclasses() {
        System.out.println("\n--- 2. Testing Room Subclasses & Business Rules ---");

        SingleRoom sr = new SingleRoom(101, 150.0);
        DoubleRoom dr = new DoubleRoom(102, 250.0);
        FamilyRoom fr = new FamilyRoom(103, 400.0);

        assertTrue("SingleRoom capacity must be 1", sr.getMaxCapacity() == 1);
        assertTrue("DoubleRoom capacity must be 2", dr.getMaxCapacity() == 2);
        assertTrue("FamilyRoom capacity must be 4", fr.getMaxCapacity() == 4);

        assertTrue("SingleRoom price for 3 days", Math.abs(sr.calculatePrice(3) - 450.0) < 0.001);

        assertThrows("calculatePrice with 0 days should throw", IllegalArgumentException.class, () -> {
            sr.calculatePrice(0);
        });

        assertThrows("Room with non-positive room number should throw", IllegalArgumentException.class, () -> {
            new SingleRoom(-1, 100.0);
        });

        assertThrows("Room with non-positive price should throw", IllegalArgumentException.class, () -> {
            new SingleRoom(104, 0.0);
        });

        assertThrows("Room with NaN price should throw", IllegalArgumentException.class, () -> {
            new SingleRoom(105, Double.NaN);
        });

        Room srCopy = new SingleRoom(101, 200.0);
        assertTrue("Rooms with identical room numbers are considered equal", sr.equals(srCopy));
    }

    private static void testStaffAndManager() {
        System.out.println("\n--- 3. Testing Staff & Manager Logic ---");

        Manager mgr = new Manager("Alice Manager", 1234, 50000.0, "mgr01", "secret123");
        mgr.getRooms().clear();
        mgr.getStaff().clear();

        mgr.addRoom(201, 100.0, Hotel.RoomType.SINGLEROOM);
        mgr.addRoom(202, 200.0, Hotel.RoomType.DOUBLEROOM);

        assertThrows("Adding duplicate room number must throw", IllegalArgumentException.class, () -> {
            mgr.addRoom(201, 150.0, Hotel.RoomType.FAMILYROOM);
        });

        Staff s1 = new Staff("Bob", 9876, 25000.0, "ST01", "Security", Staff.Shift.NIGHT);
        mgr.addStaff(s1);

        assertThrows("Adding duplicate staff ID must throw", IllegalArgumentException.class, () -> {
            Staff sDuplicate = new Staff("Bob 2", 9877, 26000.0, "st01", "Security", Staff.Shift.DAY);
            mgr.addStaff(sDuplicate);
        });

        assertTrue("Staff existence check should be case-insensitive", mgr.staffExists("st01"));

        // Test staff removal with mismatched name safety check
        boolean removeMismatch = mgr.removeStaff("Wrong Name", "ST01");
        assertTrue("Removing staff with mismatched name should safely fail", !removeMismatch);
        assertTrue("Staff still exists after mismatched removal", mgr.staffExists("ST01"));

        boolean removeSuccess = mgr.removeStaff("Bob", "ST01");
        assertTrue("Removing staff with matching name and ID should succeed", removeSuccess);
        assertTrue("Staff no longer exists after valid removal", !mgr.staffExists("ST01"));
    }

    private static void testReceptionistAndBookingWorkflow() {
        System.out.println("\n--- 4. Testing Receptionist, Bookings & Check-out Workflow ---");

        Manager mgr = new Manager("Alice Manager", 1234, 50000.0, "mgr01", "secret123");
        Receptionist rec = new Receptionist("Charlie Desk", 5678, 30000.0, "rec01", "pass123");
        mgr.getRooms().clear();
        rec.getBookings().clear();

        mgr.addRoom(301, 100.0, Hotel.RoomType.SINGLEROOM); // cap 1
        mgr.addRoom(302, 300.0, Hotel.RoomType.FAMILYROOM); // cap 4

        Room r301 = mgr.selectRoom(301);
        Room r302 = mgr.selectRoom(302);

        Guest singleGuest = new Guest("GST-01", "John Doe", 112233, 1);
        Guest largeGroup = new Guest("GST-02", "Smith Family", 445566, 3);

        // Capacity overflow check
        assertThrows("Guest group exceeding room capacity must throw", IllegalArgumentException.class, () -> {
            rec.checkIn(r301, largeGroup); // r301 capacity is 1, largeGroup is 3
        });

        // Valid check-in
        rec.checkIn(r301, singleGuest);
        assertTrue("Room 301 should now be booked (status = false)", !r301.getStatus());
        assertTrue("Guest GST-01 should be marked active", rec.hasActiveGuest("GST-01"));

        // Duplicate active guest check-in
        assertThrows("Same guest checking into another room must throw", IllegalArgumentException.class, () -> {
            rec.checkIn(r302, singleGuest);
        });

        // Room already booked check
        assertThrows("Checking in to already booked room must throw", IllegalArgumentException.class, () -> {
            Guest anotherGuest = new Guest("GST-03", "Mary", 778899, 1);
            rec.checkIn(r301, anotherGuest);
        });

        // Prevent manager from removing a currently booked room
        boolean removeBooked = mgr.removeRoom(301);
        assertTrue("Manager must NOT be able to remove a currently booked room", !removeBooked);
        assertTrue("Room 301 must still exist in manager's rooms", mgr.roomExists(301));

        // Bill generation test
        Booking b = rec.getBookings().get(0);
        String bill = rec.generateBill(b);
        assertTrue("Bill receipt contains guest name", bill.contains("John Doe"));
        assertTrue("Bill receipt contains minimum 1 day charge", bill.contains("Total Days   : 1"));

        // Check-out workflow
        int freedRoom = rec.checkOut("GST-01");
        assertTrue("Check-out should return room number 301", freedRoom == 301);
        mgr.setRoomStatus(freedRoom, true);
        assertTrue("Room 301 status is restored to available", r301.getStatus());
        assertTrue("Guest GST-01 is no longer active", !rec.hasActiveGuest("GST-01"));

        // Now that room is free, manager can remove it
        boolean removeFree = mgr.removeRoom(301);
        assertTrue("Manager can now safely remove free room 301", removeFree);
        assertTrue("Room 301 is successfully removed", !mgr.roomExists(301));
    }

    private static void testCybersecurityFileManager() {
        System.out.println("\n--- 5. Testing Cybersecurity & File Defenses (CWE-502, CWE-22, CWE-362) ---");

        // Test CWE-22: Path Traversal prevention
        assertThrows("Path traversal attempt with ../ must be blocked", SecurityException.class, () -> {
            FileManager.saveToFile("../secret.ser", new ArrayList<>());
        });

        assertThrows("Path traversal attempt with subdirectories must be blocked", SecurityException.class, () -> {
            FileManager.loadFromFile("sub/dir/exploit.ser");
        });

        // Test CWE-362: Atomic Save & File Integrity
        ArrayList<Room> testRooms = new ArrayList<>();
        testRooms.add(new SingleRoom(901, 85.0));
        testRooms.add(new DoubleRoom(902, 140.0));

        boolean saved = FileManager.saveToFile("test_rooms.ser", testRooms);
        assertTrue("Saving to valid filename should succeed", saved);
        assertTrue("Target file test_rooms.ser must exist", Files.exists(Paths.get("test_rooms.ser")));
        assertTrue("Temp file test_rooms.ser.tmp must NOT remain", !Files.exists(Paths.get("test_rooms.ser.tmp")));

        ArrayList<Room> loadedRooms = FileManager.loadFromFile("test_rooms.ser");
        assertTrue("Loaded rooms count matches saved count", loadedRooms.size() == 2);
        assertTrue("First loaded room number is 901", loadedRooms.get(0).getRoomNumber() == 901);

        // Test CWE-502: Insecure Deserialization Filter
        // Try to serialize an unauthorized class (e.g. java.util.Date or java.lang.ProcessBuilder if serializable)
        // and ensure the ObjectInputFilter safely rejects it without executing or returning it.
        try {
            Path maliciousFile = Paths.get("unauthorized_payload.ser");
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(maliciousFile))) {
                // Serialize a standard class that is NOT in our whitelist (e.g. java.util.Date or HashMap)
                ArrayList<Object> untrustedList = new ArrayList<>();
                untrustedList.add(new java.util.Date()); // Date is NOT in ALLOWED_CLASSES_FILTER
                oos.writeObject(untrustedList);
            }

            ArrayList<Object> result = FileManager.loadFromFile("unauthorized_payload.ser");
            assertTrue("Deserializing unauthorized class must be rejected by ObjectInputFilter, returning empty list", result.isEmpty());
            Files.deleteIfExists(maliciousFile);
        } catch (Exception e) {
            System.err.println("Deserialization filter test error: " + e.getMessage());
        }
    }
}
