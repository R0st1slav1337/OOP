package models;

public class Employee extends User {
    protected double salary;

    public Employee() {}

    public Employee(String id, String username, String password, String fullName, double salary) {
        super(id, username, password, fullName);
        this.salary = salary;
    }

    public void sendMessage(Employee employee, String message) {
        System.out.println(fullName + " send message to " + employee.fullName + ": " + message);
    }

    @Override
    public void showMenu() {
        System.out.println("Employee menu");
    }
    
}
