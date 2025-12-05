public abstract class Person{
    private String name;
    private String address;
    private int contactNumber;

    public Person(String name, String address, int contactNumber) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public Person(String name,int contactNumber){
        this.name = name;
        this.contactNumber = contactNumber;
    }
    public abstract boolean equals(Person person);
    public abstract String toString();

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getContactNumber() {
        return contactNumber;
    }
}