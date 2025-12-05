
import java.util.ArrayList;

public class Hotel {
    
    ArrayList<Employee> employees;

    public void addEmployee(Employee emp){
        employees.add(emp);
    }

    // public void removeEmployee(String name, String employeeID){
    //     employees.removeIf((search) -> 
    //             search.getName().equalsIgnoreCase(name) | search.getUniqueID().equalsIgnoreCase(employeeID));
    //     System.out.println("Employee with name " + name + " has been successfully remove! ");
    // }
     
    // enhance version kon sa rakhanha
    public void removeEmployee(String name, String employeeID) {
    boolean found = false;

    for (Employee emp : employees) {
        boolean nameMatch = emp.getName().equalsIgnoreCase(name);
        boolean idMatch = emp.getUniqueID().equalsIgnoreCase(employeeID);

        if (nameMatch && idMatch) {
            employees.remove(emp);
            System.out.println(" Employee removed successfully: " + name + " (" + employeeID + ")");
            found = true;
            break;
        }
        else if (nameMatch) {
            System.out.println("Name matches but ID does NOT match!");
            found = true;
        }
        else if (idMatch) {
            System.out.println(" ID matches but Name does NOT match!");
            found = true;
        }
    }

    if (!found) {
        System.out.println("No employee found with name '" + name + "' or ID '" + employeeID + "'");
    }
    }
    // public void searchEmployee(String name, String employeeID){
    //     for(Employee em : employees){
    //         if(em.getName().equalsIgnoreCase(name) | em.getUniqueID().equalsIgnoreCase(employeeID)){
    //             System.out.println("Employee with name " + name + " and ID "+  employeeID + " has been successfully removes! " );
    //         }
    //         System.out.println("Employee with name " + name + " and ID "+  employeeID + " has been not been found " );
    //     }
    // }

    public void searchEmployee(String name, String employeeID) {
    boolean found = false;

    for (Employee emp : employees) {
        boolean nameMatch = emp.getName().equalsIgnoreCase(name);
        boolean idMatch = emp.getUniqueID().equalsIgnoreCase(employeeID);

        if (nameMatch && idMatch) {
            System.out.println("Employee FOUND: Name and ID both match.");
            found = true;
            break;
        }
        else if (nameMatch) {
            System.out.println("Name matches but ID does NOT match!");
            found = true;
        }
        else if (idMatch) {
            System.out.println("ID matches but Name does NOT match!");
            found = true;
        }
    }

    if (!found) {
        System.out.println("Employee NOT found by name nor ID.");
    }

    }
}

