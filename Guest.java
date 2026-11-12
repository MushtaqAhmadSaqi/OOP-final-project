import java.util.Objects;

public class Guest extends Person {
    private static final long serialVersionUID = 1L;

    private String guestID;
    private int familyMember;

    public Guest() {
        super();
        this.guestID = "G-UNKNOWN";
        this.familyMember = 1;
    }

    public Guest(String guestID, String name, int contactNumber, int familyMember) {
        super(name, contactNumber);
        setGuestID(guestID);
        setFamilyMember(familyMember);
    }

    @Override
    public String toString() {
        return "Guest ID : " + guestID + ", Guest Name : " + getName()
                + ", Contact : " + getContactNumber() + ", Family Members : " + familyMember;
    }

    public String getGuestID() {
        return guestID;
    }

    public void setGuestID(String guestID) {
        if (guestID == null || guestID.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest ID cannot be empty.");
        }
        String cleanID = guestID.trim();
        if (cleanID.length() > 50) {
            throw new IllegalArgumentException("Guest ID cannot exceed 50 characters.");
        }
        this.guestID = cleanID;
    }

    public int getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(int familyMember) {
        if (familyMember <= 0) {
            throw new IllegalArgumentException("Family members must be at least 1.");
        }
        if (familyMember > 50) {
            throw new IllegalArgumentException("Family members count is unreasonably high.");
        }
        this.familyMember = familyMember;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guest)) return false;
        Guest guest = (Guest) o;
        return guestID != null && guestID.equalsIgnoreCase(guest.guestID);
    }

    @Override
    public int hashCode() {
        return guestID != null ? guestID.toLowerCase().hashCode() : 0;
    }
}
