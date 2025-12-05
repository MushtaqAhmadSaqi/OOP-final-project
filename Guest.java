public class Guest extends Person{
    private String guestID;
    private int familyMember;

    
    public Guest(String name, String address, int contactNumber, String guestID, int familyMember) {
        super(name, address, contactNumber);
        this.guestID = guestID;
        this.familyMember = familyMember;
    }

    public boolean equals(Person p){
        if(this.getName().equalsIgnoreCase(p.getName())){
            System.out.println("This is the same Person");
            return true;
        }
        return false;
    }

    public String toString(){
        return "Guest Name : " + getName() + "Guest ID : " + getGuestID() ;
    }

    public String getGuestID() {
        return guestID;
    }

    public int getFamilyMember() {
        return familyMember;
    }
    
}
