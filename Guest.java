public class Guest extends Person{
    private String guestID;
    private int familyMember;

    public Guest () {

    }

    public Guest(String guestID, String name, int contactNumber, int familyMember) {
        super(name, contactNumber);
        this.guestID = guestID;
        this.familyMember = familyMember;
    }

    // public Guest(String name, int contactNumber, String guestID, int familyMember) {
    //     super(name, contactNumber);
    //     this.guestID = guestID;
    //     this.familyMember = familyMember;
    // }

    @Override
    public String toString(){
        return "Guest ID : " + guestID + ", Guest Name : " + getName() 
        + ", Contact : " + getContactNumber() + ", Family Members : " + familyMember;
    }

    public String getGuestID() {
        return guestID;
    }

    public int getFamilyMember() {
        return familyMember;
    }
    

    @Override
    public boolean equals(Object o) {
        if ( o == null || !(o instanceof Guest) ) {
            return false;
        }
        Guest guest = (Guest) o;
        return guestID.equals(guest.guestID);
    }
}
