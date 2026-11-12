import java.util.Objects;

public class Staff extends Employee {
    private static final long serialVersionUID = 1L;

    private String staffID;
    private String staffDescription;
    private Shift shiftType;

    public Staff(String name, int contactNumber, double salary,
            String staffID, String staffDescription, Shift shiftType) {
        super(name, contactNumber, salary);
        setStaffID(staffID);
        setStaffDescription(staffDescription);
        setShiftType(shiftType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Staff)) return false;
        Staff s = (Staff) obj;
        return Objects.equals(staffID, s.staffID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffID);
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        if (staffID == null || staffID.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty.");
        }
        this.staffID = staffID.trim();
    }

    public String getStaffDescription() {
        return staffDescription;
    }

    public void setStaffDescription(String staffDescription) {
        if (staffDescription == null || staffDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff role/description cannot be empty.");
        }
        this.staffDescription = staffDescription.trim();
    }

    public Shift getShiftType() {
        return shiftType;
    }

    public void setShiftType(Shift shiftType) {
        if (shiftType == null) {
            throw new IllegalArgumentException("Shift type is required.");
        }
        this.shiftType = shiftType;
    }

    @Override
    public String toString() {
        return super.toString() + "\nStaff ID : " + staffID
                + ", Job Description : " + staffDescription + ", Shift Type : " + shiftType;
    }
    
    public enum Shift {
        DAY,
        NIGHT,
        ALLDAY
    }
}
