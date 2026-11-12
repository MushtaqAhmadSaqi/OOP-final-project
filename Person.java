import java.io.*;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int contactNumber;

    public Person() {

    }

    public Person(String name, int contactNumber) {
        setName(name);
        setContactNumber(contactNumber);
    }
    
    @Override
    public abstract boolean equals(Object o);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        if (contactNumber <= 0) {
            throw new IllegalArgumentException("Contact number must be positive.");
        }
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "Name : " + name + ", Contact : " + contactNumber;
    }
}
