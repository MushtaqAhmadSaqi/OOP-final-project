import java.time.LocalDate;

public abstract class Employee extends Person {
    private static final long serialVersionUID = 1L;

    private double salary;
    private LocalDate dateJoin;
    
    public Employee() {

    }
    
    public Employee(String name, int contactNumber, double salary) {
        super(name, contactNumber);
        setSalary(salary);
        this.dateJoin = LocalDate.now();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString() {
        return super.toString() + ", Salary : " + salary + ", Date Joined : " + dateJoin;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getDateJoin() {
        return dateJoin;
    }

    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
        this.salary = salary;
    }
}
