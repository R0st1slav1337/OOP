package models;

import java.util.ArrayList;
import java.util.List;

public class Admin extends Employee {
    private List<User> users = new ArrayList<>();

    public Admin(String id, String username, String password, String fullName, double salary, String department) {
        super(id, username, password, fullName, salary, department);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public void viewUsers() {
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Override
    public void showMenu() {
        System.out.println("Admin menu");
    }
}