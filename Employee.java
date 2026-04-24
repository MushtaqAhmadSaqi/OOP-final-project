import java.time.LocalDate;
public abstract class Employee extends Person{
    private double Salary;
    private LocalDate dateJoin;
    
    public Employee() {

    }
    
    public Employee(String name, int contactNumber, double Salary) {
        super(name,contactNumber);
        this.Salary = Salary;
        this.dateJoin = LocalDate.now();
    }

    @Override
    public abstract boolean equals(Object o);

    
    
    @Override
    public String toString(){
        return super.toString() + ", Salary : " + Salary + ", Date Joined : " + dateJoin;
    }

    public double getSalary() {
        return Salary;
    }

    public LocalDate getDateJoin() {
        return dateJoin;
    }

    public void setSalary(double salary) {
        Salary = salary;
    }
    
}
    
    
