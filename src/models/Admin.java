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
    }

    public void removeUser(Database database, User user) {
        database.removeUser(user);
    }

    public void viewUsers(Database database) {
        List<User> users = database.getUsers();

        for (User user : users) {
            System.out.println(user);
        }
    }

    @Override
    public void showMenu() {
        System.out.println("Admin menu");
    }
}