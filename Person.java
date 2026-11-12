import java.io.*;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int contactNumber;

    public Person() {
        this.name = "Unknown";
        this.contactNumber = 1;
    }

    public Person(String name, int contactNumber) {
        setName(name);
        setContactNumber(contactNumber);
    }
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        String cleanName = name.trim();
        if (cleanName.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters.");
        }
        this.name = cleanName;
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
        return "Name : " + (name != null ? name : "Unknown") + ", Contact : " + contactNumber;
    }
}
