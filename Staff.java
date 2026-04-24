public class Staff extends Employee{
    private String staffID;
    private String staffDescription;
    private Shift shiftType;

    public Staff(String name, int contactNumber, double Salary,
            String staffID, String staffDescription, Shift shiftType) {
        super(name, contactNumber, Salary);
        this.staffID = staffID;
        this.staffDescription = staffDescription;
        this.shiftType = shiftType;
    }
    @Override
    public boolean equals(Object obj) {
        if ( obj == null || !(obj instanceof Staff) ) {
            return false;
        }
        Staff s = (Staff) obj;
        return staffID.equals(s.staffID);
        
    }

    public String getStaffID() {
        return staffID;
    }

    // public void setStaffID(String staffID) {
    //     this.staffID = staffID;
    // }

    public String getStaffDescription() {
        return staffDescription;
    }

    public void setStaffDescription(String staffDescription) {
        this.staffDescription = staffDescription;
    }

    public Shift getShiftType() {
        return shiftType;
    }

    public void setShiftType(Shift shiftType) {
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