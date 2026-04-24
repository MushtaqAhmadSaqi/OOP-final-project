import java.io.*;
public abstract class Person implements Serializable{
    private String name;
    private int contactNumber;

    public Person() {

    }

    public Person(String name, int contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }
    
    @Override
    public abstract boolean equals(Object o);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "Name : " + name + ", Contact : " + contactNumber;
    }
}
