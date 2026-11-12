import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HotelMainGUI extends JFrame {

    private Manager manager;
    private Receptionist receptionist;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JTabbedPane managerTabs;
    private JTabbedPane receptionistTabs;

    private final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 24);
    private final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); 

    private JTable roomTable, staffTable;
    private DefaultTableModel roomModel, staffModel;
    private JTable bookingTable;
    private DefaultTableModel bookingModel;
    private JTextArea availableRoomsArea;

    public HotelMainGUI() {
        manager = new Manager("Admin", 1, 0.0, "Saqi", "123");
        receptionist = new Receptionist("FrontDesk", 1, 0.0, "Saqi", "123");

        manager.load(); 
        receptionist.load();
        synchronizeRoomStatus();

        setTitle("Hotel Management System - GUI Edition");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createManagerPanel();
        createReceptionistPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(managerTabs, "MANAGER");
        mainPanel.add(receptionistTabs, "RECEPTIONIST");

        add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(HotelMainGUI.this, 
                    "Are you sure you want to exit? All data will be saved.", "Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    saveAll();
                    System.exit(0);
                }
            }
        });
    }

    private void saveAll() {
        if (manager != null) manager.save();
        if (receptionist != null) receptionist.save();
    }

    private void synchronizeRoomStatus() {
        if (manager == null || receptionist == null || manager.getRooms() == null || receptionist.getBookings() == null) {
            return;
        }

        for (Room room : manager.getRooms()) {
            room.setStatus(true);
        }

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

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Hotel Management System");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        String[] roles = {"Manager", "Receptionist"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(NORMAL_FONT);
        gbc.gridy = 1;
        loginPanel.add(roleCombo, gbc);

        JTextField idField = new JTextField(15);
        idField.setBorder(BorderFactory.createTitledBorder("User ID"));
        gbc.gridy = 2;
        loginPanel.add(idField, gbc);

        JPasswordField passField = new JPasswordField(15);
        passField.setBorder(BorderFactory.createTitledBorder("Password"));
        gbc.gridy = 3;
        loginPanel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(PRIMARY_COLOR);
        loginBtn.setForeground(Color.WHITE);
        gbc.gridy = 4;
        loginPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String id = idField.getText() != null ? idField.getText().trim() : "";
            String pass = new String(passField.getPassword());

            if ("Manager".equals(role)) {
                if (id.equalsIgnoreCase(manager.getManagerID()) && constantTimePasswordCheck(pass, manager.getPassCode())) {
                    synchronizeRoomStatus();
                    cardLayout.show(mainPanel, "MANAGER");
                    refreshManagerTables();
                    refreshReceptionistTables();
                    passField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Manager Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (id.equalsIgnoreCase(receptionist.getReceptID()) && constantTimePasswordCheck(pass, receptionist.getPassCode())) {
                    synchronizeRoomStatus();
                    cardLayout.show(mainPanel, "RECEPTIONIST");
                    refreshReceptionistTables();
                    refreshManagerTables();
                    passField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Receptionist Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void createManagerPanel() {
        managerTabs = new JTabbedPane();
        
        // Rooms panel
        JPanel roomPanel = new JPanel(new BorderLayout());
        String[] roomCols = {"Room No", "Type", "Price", "Status"};
        roomModel = new DefaultTableModel(roomCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomTable = new JTable(roomModel);
        roomPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JPanel roomBtns = new JPanel();
        JButton addRoomBtn = new JButton("Add Room");
        JButton searchRoomBtn = new JButton("Search Room"); 
        JButton displayAllRoomBtn = new JButton("Refresh Rooms"); 
        JButton removeRoomBtn = new JButton("Remove Room");
        JButton logoutM = new JButton("Logout");

        roomBtns.add(addRoomBtn);
        roomBtns.add(searchRoomBtn);
        roomBtns.add(displayAllRoomBtn);
        roomBtns.add(removeRoomBtn);
        roomBtns.add(logoutM);
        roomPanel.add(roomBtns, BorderLayout.SOUTH);

        addRoomBtn.addActionListener(e -> showAddRoomDialog());
        
        searchRoomBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter Room Number to Search:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int rNo = Integer.parseInt(input.trim());
                    Room r = manager.selectRoom(rNo);
                    if (r != null) {
                        JOptionPane.showMessageDialog(this, "Room Found:\n" + r.toString(), "Room Details", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Room " + rNo + " not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric room number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        displayAllRoomBtn.addActionListener(e -> refreshManagerTables());

        removeRoomBtn.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row != -1) {
                int roomNo = (int) roomModel.getValueAt(row, 0);
                Room r = manager.selectRoom(roomNo);
                if (r != null && !r.getStatus()) {
                    JOptionPane.showMessageDialog(this, "Room " + roomNo + " cannot be removed because it is currently booked!", "Cannot Remove", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove Room " + roomNo + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (manager.removeRoom(roomNo)) {
                        saveAll();
                        refreshManagerTables();
                        refreshReceptionistTables();
                        JOptionPane.showMessageDialog(this, "Room " + roomNo + " removed successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not remove Room " + roomNo + ".", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a room from the table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Staff panel
        JPanel staffPanel = new JPanel(new BorderLayout());
        String[] staffCols = {"ID", "Name", "Role", "Shift", "Salary"};
        staffModel = new DefaultTableModel(staffCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        staffTable = new JTable(staffModel);
        staffPanel.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        JPanel staffBtns = new JPanel();
        JButton addStaffBtn = new JButton("Add Staff");
        JButton searchStaffBtn = new JButton("Search Staff"); 
        JButton removeStaffBtn = new JButton("Remove Staff");
        
        staffBtns.add(addStaffBtn);
        staffBtns.add(searchStaffBtn);
        staffBtns.add(removeStaffBtn);
        staffPanel.add(staffBtns, BorderLayout.SOUTH);

        addStaffBtn.addActionListener(e -> showAddStaffDialog());

        searchStaffBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Enter Staff ID to Search:");
            if (id != null && !id.trim().isEmpty()) {
                String cleanID = id.trim();
                boolean found = false;
                for (Staff s : manager.getStaff()) {
                    if (s.getStaffID().equalsIgnoreCase(cleanID)) {
                        JOptionPane.showMessageDialog(this, "Staff Found:\n" + s.toString(), "Staff Details", JOptionPane.INFORMATION_MESSAGE);
                        found = true;
                        break;
                    }
                }
                if (!found) JOptionPane.showMessageDialog(this, "Staff member with ID '" + cleanID + "' not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        });

        removeStaffBtn.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                String id = (String) staffModel.getValueAt(row, 0);
                String name = (String) staffModel.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove staff member: " + name + " (ID: " + id + ")?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (manager.removeStaff(name, id)) {
                        saveAll();
                        refreshManagerTables();
                        JOptionPane.showMessageDialog(this, "Staff member removed successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to remove staff member.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a staff member from the table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        logoutM.addActionListener(e -> {
            saveAll();
            cardLayout.show(mainPanel, "LOGIN");
        });

        managerTabs.addTab("Manage Rooms", roomPanel);
        managerTabs.addTab("Manage Staff", staffPanel);
    }

    private void showAddRoomDialog() {
        JTextField noField = new JTextField();
        JTextField priceField = new JTextField();
        String[] types = {"SINGLEROOM", "DOUBLEROOM", "FAMILYROOM"};
        JComboBox<String> typeCombo = new JComboBox<>(types);

        Object[] message = {"Room No:", noField, "Price per Night (RS):", priceField, "Type:", typeCombo};

        int option = JOptionPane.showConfirmDialog(this, message, "Add Room", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String noStr = noField.getText() != null ? noField.getText().trim() : "";
                String priceStr = priceField.getText() != null ? priceField.getText().trim() : "";

                if (noStr.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rNo = Integer.parseInt(noStr);
                double price = Double.parseDouble(priceStr);
                Hotel.RoomType rt = Hotel.RoomType.valueOf((String) typeCombo.getSelectedItem());

                manager.addRoom(rNo, price, rt);
                saveAll();
                refreshManagerTables();
                refreshReceptionistTables();
                JOptionPane.showMessageDialog(this, "Room " + rNo + " added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Room Number and Price must be valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Adding Room", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddStaffDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField descField = new JTextField();
        String[] shifts = {"DAY", "NIGHT", "ALLDAY"};
        JComboBox<String> shiftCombo = new JComboBox<>(shifts);

        Object[] message = {
            "Staff ID:", idField, "Name:", nameField, "Contact Number (Numeric):", phoneField,
            "Salary (RS):", salaryField, "Role / Description:", descField, "Shift:", shiftCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Staff", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText() != null ? idField.getText().trim() : "";
                String name = nameField.getText() != null ? nameField.getText().trim() : "";
                String phoneStr = phoneField.getText() != null ? phoneField.getText().trim() : "";
                String salaryStr = salaryField.getText() != null ? salaryField.getText().trim() : "";
                String desc = descField.getText() != null ? descField.getText().trim() : "";

                if (id.isEmpty() || name.isEmpty() || phoneStr.isEmpty() || salaryStr.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int contact = Integer.parseInt(phoneStr);
                double sal = Double.parseDouble(salaryStr);
                Staff.Shift s = Staff.Shift.valueOf((String) shiftCombo.getSelectedItem());

                Staff newStaff = new Staff(name, contact, sal, id, desc, s);
                manager.addStaff(newStaff);
                saveAll();
                refreshManagerTables();
                JOptionPane.showMessageDialog(this, "Staff Added Successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Contact Number and Salary must be valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Adding Staff", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createReceptionistPanel() {
        receptionistTabs = new JTabbedPane();

        JPanel operationPanel = new JPanel(new BorderLayout());
        
        availableRoomsArea = new JTextArea(15, 25);
        availableRoomsArea.setEditable(false);
        availableRoomsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        availableRoomsArea.setBorder(BorderFactory.createTitledBorder("Available Rooms (Live Display)"));
        operationPanel.add(new JScrollPane(availableRoomsArea), BorderLayout.WEST);

        JPanel buttons = new JPanel(new GridLayout(6, 1, 10, 10)); 
        JButton checkInBtn = new JButton("Check-In Guest");
        JButton checkOutBtn = new JButton("Check-Out Guest");
        JButton searchBookBtn = new JButton("Search Booking"); 
        JButton searchGuestBtn = new JButton("Search Guest"); 
        JButton modifyBookBtn = new JButton("Modify Booking"); 
        JButton refreshBtn = new JButton("Display All / Refresh"); 
        JButton logoutR = new JButton("Logout");

        buttons.add(checkInBtn);
        buttons.add(checkOutBtn);
        buttons.add(searchBookBtn);
        buttons.add(searchGuestBtn);
        buttons.add(modifyBookBtn);
        buttons.add(refreshBtn);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttons, BorderLayout.NORTH);
        centerPanel.add(logoutR, BorderLayout.SOUTH);
        operationPanel.add(centerPanel, BorderLayout.CENTER);

        checkInBtn.addActionListener(e -> showCheckInDialog());
        
        checkOutBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Enter Guest ID to Check-Out:");
            if (id != null && !id.trim().isEmpty()) {
                String cleanID = id.trim();
                Booking targetBooking = null;
                for (Booking b : receptionist.getBookings()) {
                    if (b.getGuests() != null && b.getGuests().getGuestID().equalsIgnoreCase(cleanID)) {
                        targetBooking = b;
                        break;
                    }
                }
        
                if (targetBooking != null) {
                    String billText = receptionist.generateBill(targetBooking);
                    JTextArea receiptArea = new JTextArea(billText);
                    receiptArea.setEditable(false);
                    receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    JOptionPane.showMessageDialog(this, new JScrollPane(receiptArea), "Bill Receipt", JOptionPane.INFORMATION_MESSAGE);
                    
                    int roomNum = receptionist.checkOut(cleanID);
                    if (roomNum != -1) {
                        manager.setRoomStatus(roomNum, true);
                        saveAll();
                        refreshReceptionistTables();
                        refreshManagerTables();
                        JOptionPane.showMessageDialog(this, "Guest " + cleanID + " checked out successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No active booking found for Guest ID: " + cleanID, "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        searchBookBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter Room Number to find Booking:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int rNo = Integer.parseInt(input.trim());
                    boolean found = false;
                    for (Booking b : receptionist.getBookings()) {
                        if (b.getRoom() != null && b.getRoom().getRoomNumber() == rNo) {
                            JOptionPane.showMessageDialog(this, "Booking Found:\n" + b.toString(), "Booking Details", JOptionPane.INFORMATION_MESSAGE);
                            found = true;
                            break;
                        }
                    }
                    if (!found) JOptionPane.showMessageDialog(this, "No booking found for room " + rNo, "Not Found", JOptionPane.WARNING_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric room number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchGuestBtn.addActionListener(e -> {
            String inputID = JOptionPane.showInputDialog(this, "Enter Guest ID or Name:");
            if (inputID != null && !inputID.trim().isEmpty()) {
                String cleanInput = inputID.trim();
                boolean found = false;
                StringBuilder sb = new StringBuilder();
                for (Booking b : receptionist.getBookings()) {
                    if (b.getGuests() != null && 
                        (b.getGuests().getGuestID().equalsIgnoreCase(cleanInput) || b.getGuests().getName().equalsIgnoreCase(cleanInput))) {
                        sb.append("Guest ID: ").append(b.getGuests().getGuestID())
                          .append(", Name: ").append(b.getGuests().getName())
                          .append(", Room: ").append(b.getRoom() != null ? b.getRoom().getRoomNumber() : "N/A")
                          .append(", Checked in: ").append(b.getCheckInDate())
                          .append("\n\n");
                        found = true;
                    }
                }
                if (found) {
                    JOptionPane.showMessageDialog(this, "Matching Guest Record(s):\n\n" + sb.toString(), "Guest Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No active guest found for '" + cleanInput + "'.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        modifyBookBtn.addActionListener(e -> showModifyBookingDialog());

        refreshBtn.addActionListener(e -> {
            synchronizeRoomStatus();
            refreshReceptionistTables();
            refreshManagerTables();
        });

        logoutR.addActionListener(e -> {
            saveAll();
            cardLayout.show(mainPanel, "LOGIN");
        });

        bookingModel = new DefaultTableModel(new String[]{"Room", "Guest ID", "Guest Name", "Contact", "Check-In Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(bookingModel);
        
        receptionistTabs.addTab("Operations & Avail. Rooms", operationPanel);
        receptionistTabs.addTab("View All Bookings", new JScrollPane(bookingTable));
    }

    private void showCheckInDialog() {
        JTextField guestIdF = new JTextField();
        JTextField nameF = new JTextField();
        JTextField contactF = new JTextField();
        JTextField familyF = new JTextField();
        JTextField roomNoF = new JTextField();

        Object[] message = {
            "Room Number:", roomNoF,
            "Guest ID:", guestIdF,
            "Guest Name:", nameF,
            "Contact Number (Numeric):", contactF,
            "Family Members (Group Size):", familyF
        };

        int opt = JOptionPane.showConfirmDialog(this, message, "Guest Check-In", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String rNoStr = roomNoF.getText() != null ? roomNoF.getText().trim() : "";
                String gId = guestIdF.getText() != null ? guestIdF.getText().trim() : "";
                String name = nameF.getText() != null ? nameF.getText().trim() : "";
                String contactStr = contactF.getText() != null ? contactF.getText().trim() : "";
                String famStr = familyF.getText() != null ? familyF.getText().trim() : "";

                if (rNoStr.isEmpty() || gId.isEmpty() || name.isEmpty() || contactStr.isEmpty() || famStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All check-in fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rNo = Integer.parseInt(rNoStr);
                Room r = manager.selectRoom(rNo);

                if (r == null) {
                    JOptionPane.showMessageDialog(this, "Room " + rNo + " does not exist in the hotel system.", "Room Not Found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!r.isAvailable()) {
                    JOptionPane.showMessageDialog(this, "Room " + rNo + " is already booked.", "Room Unavailable", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int contact = Integer.parseInt(contactStr);
                int fam = Integer.parseInt(famStr);
                
                Guest g = new Guest(gId, name, contact, fam);
                receptionist.checkIn(r, g);
                saveAll();
                refreshReceptionistTables();
                refreshManagerTables();
                JOptionPane.showMessageDialog(this, "Guest Checked-In Successfully for Room " + rNo + "!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Room Number, Contact, and Family Members must be valid whole numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Check-In Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showModifyBookingDialog() {
        String inputRoom = JOptionPane.showInputDialog(this, "Enter Room Number to Modify:");
        if (inputRoom == null || inputRoom.trim().isEmpty()) return;

        try {
            int rNo = Integer.parseInt(inputRoom.trim());
            Booking targetBooking = null;

            for (Booking b : receptionist.getBookings()) {
                if (b.getRoom() != null && b.getRoom().getRoomNumber() == rNo) {
                    targetBooking = b;
                    break;
                }
            }

            if (targetBooking == null) {
                JOptionPane.showMessageDialog(this, "No active booking found for room " + rNo, "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Guest currentGuest = targetBooking.getGuests();
            JTextField guestIdF = new JTextField(currentGuest != null ? currentGuest.getGuestID() : "");
            JTextField nameF = new JTextField(currentGuest != null ? currentGuest.getName() : "");
            JTextField contactF = new JTextField(currentGuest != null ? String.valueOf(currentGuest.getContactNumber()) : "");
            JTextField familyF = new JTextField(currentGuest != null ? String.valueOf(currentGuest.getFamilyMember()) : "1");

            Object[] message = {
                "Modifying Room " + rNo + " (Capacity: " + (targetBooking.getRoom() != null ? targetBooking.getRoom().getMaxCapacity() : "N/A") + ")",
                "New / Updated Guest ID:", guestIdF,
                "New / Updated Name:", nameF, 
                "New / Updated Contact (Numeric):", contactF,
                "New / Updated Family Members:", familyF
            };

            int opt = JOptionPane.showConfirmDialog(this, message, "Modify Guest Details", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                String gId = guestIdF.getText() != null ? guestIdF.getText().trim() : "";
                String name = nameF.getText() != null ? nameF.getText().trim() : "";
                String contactStr = contactF.getText() != null ? contactF.getText().trim() : "";
                String famStr = familyF.getText() != null ? familyF.getText().trim() : "";

                if (gId.isEmpty() || name.isEmpty() || contactStr.isEmpty() || famStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int contact = Integer.parseInt(contactStr);
                int fam = Integer.parseInt(famStr);
                
                Guest newGuest = new Guest(gId, name, contact, fam);
                if (receptionist.modifyGuestInBooking(rNo, newGuest)) {
                    saveAll();
                    refreshReceptionistTables();
                    refreshManagerTables();
                    JOptionPane.showMessageDialog(this, "Booking for Room " + rNo + " updated successfully!");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Room Number, Contact, and Family Members must be valid whole numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Modification Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshManagerTables() {
        roomModel.setRowCount(0);
        for (Room r : manager.getRooms()) {
            String type = "Unknown";
            if (r instanceof SingleRoom) type = "Single";
            else if (r instanceof DoubleRoom) type = "Double";
            else if (r instanceof FamilyRoom) type = "Family";

            roomModel.addRow(new Object[]{r.getRoomNumber(), type, String.format("RS.%.2f", r.getPricePerNight()), r.AvailableOrBooked()});
        }

        staffModel.setRowCount(0);
        if (manager.getStaff() != null) {
            for (Staff s : manager.getStaff()) {
                staffModel.addRow(new Object[]{
                    s.getStaffID(), s.getName(), s.getStaffDescription(), s.getShiftType(), String.format("RS.%.2f", s.getSalary())
                });
            }
        }
    }

    private void refreshReceptionistTables() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Room r : manager.getRooms()) {
            if (r.isAvailable()) {
                sb.append(String.format("Room %-4d | %-8s | RS.%.2f (Cap: %d)\n", 
                    r.getRoomNumber(), 
                    (r instanceof SingleRoom ? "Single" : r instanceof DoubleRoom ? "Double" : "Family"),
                    r.getPricePerNight(),
                    r.getMaxCapacity()));
                count++;
            }
        }
        if (count == 0) {
            availableRoomsArea.setText("No rooms currently available.\nAll rooms are booked or none added.");
        } else {
            availableRoomsArea.setText("Total Available: " + count + " room(s)\n----------------------------------------\n" + sb.toString());
        }

        bookingModel.setRowCount(0);
        if (receptionist.getBookings() != null) {
            for (Booking b : receptionist.getBookings()) {
                bookingModel.addRow(new Object[]{
                    b.getRoom() != null ? b.getRoom().getRoomNumber() : "N/A", 
                    b.getGuests() != null ? b.getGuests().getGuestID() : "N/A",     
                    b.getGuests() != null ? b.getGuests().getName() : "N/A", 
                    b.getGuests() != null ? b.getGuests().getContactNumber() : "N/A",
                    b.getCheckInDate() != null ? b.getCheckInDate().toString() : "N/A"
                });
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { /* Fallback to default look and feel */ }

        SwingUtilities.invokeLater(() -> {
            new HotelMainGUI().setVisible(true);
        });
    }
}
