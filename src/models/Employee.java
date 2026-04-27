package models;

public class Employee extends User {
    protected double salary;
    protected String department;

    public Employee() {}

    public Employee(String id, String username, String password, 
                    String fullName, double salary, String department) {
        super(id, username, password, fullName);
        this.salary = salary;
        this.department = department;
    }

    public void sendMessage(Employee employee, String message) {
        System.out.println(fullName + " send message to " + employee.fullName + ": " + message);
    }

    public double getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public void showMenu() {
        System.out.println("Employee menu");
    }
    
}
