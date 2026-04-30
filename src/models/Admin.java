package models;

import java.util.ArrayList;
import java.util.List;

public class Admin extends Employee {
    private List<User> users = new ArrayList<>();

    public Admin(String id, String username, String password, String fullName, double salary, String department) {
        super(id, username, password, fullName, salary, department);
    }

    public void addUser(Database database, User user) {
        database.addUser(user);
        database.addLog("ADMIN: " + getFullName() + " added user " + user.getFullName());
    }

    public void removeUser(Database database, User user) {
        database.removeUser(user);
        database.addLog("ADMIN: " + getFullName() + " removed user " + user.getFullName());
    }

    public void viewUsers(Database database) {
        List<User> users = database.getUsers();

        for (User user : users) {
            System.out.println(user);
        }
    }

    public void viewLogs(Database database) {
        database.printLogs();
    }

    @Override
    public void showMenu() {
        System.out.println("Admin menu");
    }
}