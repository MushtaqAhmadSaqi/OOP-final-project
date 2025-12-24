import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    public HotelMainGUI() {
        manager = new Manager("Admin", 0000, 0.0, "Saqi", "123");
        receptionist = new Receptionist("FrontDesk", 0000, 0.0, "Saqi", "123");
        

        manager.load(); 
        receptionist.load();

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
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to exit? Data will be saved.", "Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.save();
                    receptionist.save();
                    System.exit(0);
                }
            }
        });
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
            String id = idField.getText();
            String pass = new String(passField.getPassword());

            if (role.equals("Manager")) {
                if (id.equals(manager.getManagerID()) && pass.equals(manager.getPassCode())) {
                    cardLayout.show(mainPanel, "MANAGER");
                    refreshManagerTables();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Manager Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (id.equals(receptionist.getReceptID()) && pass.equals(receptionist.getPassCode())) {
                    cardLayout.show(mainPanel, "RECEPTIONIST");
                    refreshReceptionistTables();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Receptionist Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // manager module
    private JTable roomTable, staffTable;
    private DefaultTableModel roomModel, staffModel;

    private void createManagerPanel() {
        managerTabs = new JTabbedPane();
        
        JPanel roomPanel = new JPanel(new BorderLayout());
        
        String[] roomCols = {"Room No", "Type", "Price", "Status"};
        roomModel = new DefaultTableModel(roomCols, 0);
        roomTable = new JTable(roomModel);
        roomPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JPanel roomBtns = new JPanel();
        JButton addRoomBtn = new JButton("Add Room");
        JButton searchRoomBtn = new JButton("Search Room"); 
        JButton displayAllRoomBtn = new JButton("Display All Rooms"); 
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
            String input = JOptionPane.showInputDialog("Enter Room Number to Search:");
            if (input != null) {
                try {
                    int rNo = Integer.parseInt(input);
                    Room r = manager.selectRoom(rNo);
                    if (r != null) {
                        JOptionPane.showMessageDialog(this, "Room Found:\n" + r.toString());
                    } else {
                        JOptionPane.showMessageDialog(this, "Room not found.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Number.");
                }
            }
        });


        displayAllRoomBtn.addActionListener(e -> refreshManagerTables());

        removeRoomBtn.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row != -1) {
                int roomNo = (int) roomModel.getValueAt(row, 0);
                manager.removeRoom(roomNo);
                refreshManagerTables();
            } else {
                JOptionPane.showMessageDialog(this, "Select a room from the table first.");
            }
        });


        JPanel staffPanel = new JPanel(new BorderLayout());
        
        String[] staffCols = {"ID", "Name", "Role", "Shift", "Salary"};
        staffModel = new DefaultTableModel(staffCols, 0);
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
            String id = JOptionPane.showInputDialog("Enter Staff ID to Search:");
            if (id != null) {
                boolean found = false;
                for (Staff s : manager.getStaff()) {
                    if (s.getStaffID().equalsIgnoreCase(id)) {
                        JOptionPane.showMessageDialog(this, "Staff Found:\n" + s.toString());
                        found = true;
                        break;
                    }
                }
                if (!found) JOptionPane.showMessageDialog(this, "Staff member not found.");
            }
        });

        removeStaffBtn.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                String id = (String) staffModel.getValueAt(row, 0);
                String name = (String) staffModel.getValueAt(row, 1);
                manager.removeStaff(name, id);
                refreshManagerTables();
            }
        });

        logoutM.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        managerTabs.addTab("Manage Rooms", roomPanel);
        managerTabs.addTab("Manage Staff", staffPanel);
    }

    private void showAddRoomDialog() {
        JTextField noField = new JTextField();
        JTextField priceField = new JTextField();
        String[] types = {"SINGLEROOM", "DOUBLEROOM", "FAMILYROOM"};
        JComboBox<String> typeCombo = new JComboBox<>(types);

        Object[] message = {"Room No:", noField, "Price:", priceField, "Type:", typeCombo};

        int option = JOptionPane.showConfirmDialog(null, message, "Add Room", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int rNo = Integer.parseInt(noField.getText());
                double price = Double.parseDouble(priceField.getText());
                Hotel.RoomType rt = Hotel.RoomType.valueOf((String) typeCombo.getSelectedItem());
                manager.addRoom(rNo, price, rt);
                refreshManagerTables();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Input!");
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
            "Staff ID:", idField, "Name:", nameField, "Phone (Int):", phoneField,
            "Salary:", salaryField, "Role:", descField, "Shift:", shiftCombo
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Staff", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                double sal = Double.parseDouble(salaryField.getText());

                int contact = Integer.parseInt(phoneField.getText());
                Staff.Shift s = Staff.Shift.valueOf((String)shiftCombo.getSelectedItem());
                

                Staff newStaff = new Staff(nameField.getText(), contact, sal, idField.getText(), descField.getText(), s);
                manager.addStaff(newStaff);
                
                refreshManagerTables(); // This updates the GUI table
                JOptionPane.showMessageDialog(this, "Staff Added Successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Phone and Salary must be numbers!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // receptionist module
    private JTable bookingTable;
    private DefaultTableModel bookingModel;
    private JTextArea availableRoomsArea;

    private void createReceptionistPanel() {
        receptionistTabs = new JTabbedPane();


        JPanel operationPanel = new JPanel(new BorderLayout());
        
        availableRoomsArea = new JTextArea(15, 25);
        availableRoomsArea.setEditable(false);
        availableRoomsArea.setBorder(BorderFactory.createTitledBorder("Available Rooms (Display)"));
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
            String id = JOptionPane.showInputDialog("Enter Guest ID to Check-Out:");
            if (id != null) {
                String billText = "";
                boolean found = false;
                
                for (Booking b : receptionist.getBookings()) {
                    if (b.getGuests().getGuestID().equals(id)) {
                        billText = receptionist.generateBill(b); 
                        found = true;
                        break;
                    }
                }
        
                if (found) {
                    JOptionPane.showMessageDialog(this, new JTextArea(billText), "Bill Receipt", JOptionPane.INFORMATION_MESSAGE);
                    
                    int roomNum = receptionist.checkOut(id);
                    if (roomNum != -1) {
                        manager.setRoomStatus(roomNum, true);
                        refreshReceptionistTables();
                        refreshManagerTables();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Guest not found.");
                }
            }
        });


        searchBookBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter Room Number to find Booking:");
            if(input != null) {
                try {
                    int rNo = Integer.parseInt(input);
                    boolean found = false;
                    for(Booking b : receptionist.getBookings()) {
                        if(b.getRoom().getRoomNumber() == rNo) {
                            JOptionPane.showMessageDialog(this, "Booking Found:\n" + b.toString());
                            found = true;
                            break;
                        }
                    }
                    if(!found) JOptionPane.showMessageDialog(this, "No booking found for room " + rNo);
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Number"); }
            }
        });


        searchGuestBtn.addActionListener(e -> {
            String inputID = JOptionPane.showInputDialog("Enter Guest ID:");
            if(inputID != null) {
                boolean found = false;
                for(Booking b : receptionist.getBookings()) {
                    if(b.getGuests().getGuestID().equalsIgnoreCase(inputID)) { 
                        JOptionPane.showMessageDialog(this, "Guest Found in Room " + b.getRoom().getRoomNumber() + ":\n" + b.getGuests());
                        found = true;
                    }
                }
                if(!found) JOptionPane.showMessageDialog(this, "Guest ID not currently checked in.");
            }
        });


        modifyBookBtn.addActionListener(e -> showModifyBookingDialog());

        refreshBtn.addActionListener(e -> refreshReceptionistTables());
        logoutR.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));


        bookingModel = new DefaultTableModel(new String[]{"Room", "Guest ID", "Guest Name", "Contact"}, 0);
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
            "Guest ID:", guestIdF, "Name:", nameF, "Contact (Int):", contactF,
            "Family Members:", familyF, "Room Number:", roomNoF
        };

        int opt = JOptionPane.showConfirmDialog(null, message, "Check-In", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                int rNo = Integer.parseInt(roomNoF.getText());
                Room r = manager.selectRoom(rNo);

                if (r != null && r.isAvailable()) {

                    int contact = Integer.parseInt(contactF.getText());
                    int fam = Integer.parseInt(familyF.getText());
                    
                    Guest g = new Guest(guestIdF.getText(), nameF.getText(), contact, fam);
                    
                    receptionist.checkIn(r, g);
                    refreshReceptionistTables();
                    JOptionPane.showMessageDialog(this, "Check-In Successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Room not available!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Contact, Family, Room must be numbers!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showModifyBookingDialog() {
        String inputRoom = JOptionPane.showInputDialog("Enter Room Number to Modify:");
        if (inputRoom == null) return;

        try {
            int rNo = Integer.parseInt(inputRoom);
            boolean found = false;

            for(Booking b : receptionist.getBookings()) {
                if(b.getRoom().getRoomNumber() == rNo) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                JOptionPane.showMessageDialog(this, "No booking found for room " + rNo);
                return;
            }

            JTextField guestIdF = new JTextField();
            JTextField nameF = new JTextField();
            JTextField contactF = new JTextField();
            JTextField familyF = new JTextField();

            Object[] message = {
                "NEW Guest ID:", guestIdF, "NEW Name:", nameF, 
                "NEW Contact (Int):", contactF, "NEW Family Members:", familyF
            };

            int opt = JOptionPane.showConfirmDialog(null, message, "Modify Guest Details", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                int contact = Integer.parseInt(contactF.getText());
                int fam = Integer.parseInt(familyF.getText());
                
                Guest newGuest = new Guest(guestIdF.getText(), nameF.getText(), contact, fam);
                
                receptionist.modifyGuestInBooking(rNo, newGuest);
                refreshReceptionistTables();
                JOptionPane.showMessageDialog(this, "Booking Updated!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage());
        }
    }

    
    private void refreshManagerTables() {

        roomModel.setRowCount(0);
        for (Room r : manager.getRooms()) {
            String type = "Unknown";
            if (r instanceof SingleRoom) type = "Single";
            else if (r instanceof DoubleRoom) type = "Double";
            else if (r instanceof FamilyRoom) type = "Family";

            roomModel.addRow(new Object[]{r.getRoomNumber(), type, r.getPricePerNight(), r.AvailableOrBooked()});
        }

        staffModel.setRowCount(0);
        if (manager.getStaff() != null) {
            for (Staff s : manager.getStaff()) {
                staffModel.addRow(new Object[]{
                    s.getStaffID(), s.getName(), s.getStaffDescription(), s.getShiftType(), s.getSalary()
                });
            }
        }
    }

    private void refreshReceptionistTables() {

        StringBuilder sb = new StringBuilder();
        for (Room r : manager.getRooms()) {
            if (r.isAvailable()) {
                sb.append("Room ").append(r.getRoomNumber())
                  .append(" - RS.").append(r.getPricePerNight()).append("\n");
            }
        }
        availableRoomsArea.setText(sb.toString());


        bookingModel.setRowCount(0);
        if (receptionist.getBookings() != null) {
            for (Booking b : receptionist.getBookings()) {
                bookingModel.addRow(new Object[]{
                    b.getRoom().getRoomNumber(), 
                    b.getGuests().getGuestID(),     
                    b.getGuests().getName(), 
                    b.getGuests().getContactNumber()
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
        } catch (Exception e) { /* Ignore */ }

        SwingUtilities.invokeLater(() -> {
            new HotelMainGUI().setVisible(true);
        });
    }
}