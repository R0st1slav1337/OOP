package models;

import java.util.List;

public class Admin extends Employee {

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

    public void updateUserUsername(Database database, User user, String newUsername) {
        if (user == null) {
            System.out.println("User does not exist.");
            return;
        }

        if (newUsername == null || newUsername.isBlank()) {
            System.out.println("New username cannot be empty.");
            return;
        }

        if (!user.getUsername().equalsIgnoreCase(newUsername) && database.isUsernameTaken(newUsername)) {
            System.out.println("Username is already taken.");

            database.addLog(
                    "ADMIN: " + getFullName() +
                            " failed to update username for " + user.getFullName() +
                            " because username '" + newUsername + "' is already taken."
            );

            return;
        }

        String oldUsername = user.getUsername();
        user.setUsername(newUsername);

        database.addLog(
                "ADMIN: " + getFullName() +
                        " changed username of " + user.getFullName() +
                        " from '" + oldUsername + "' to '" + newUsername + "'"
        );

        System.out.println("Username updated successfully.");
    }

    public void updateUserPassword(Database database, User user, String newPassword) {
        if (user == null) {
            System.out.println("User does not exist.");
            return;
        }

        if (newPassword == null || newPassword.isBlank()) {
            System.out.println("New password cannot be empty.");
            return;
        }

        user.setPassword(newPassword);

        database.addLog(
                "ADMIN: " + getFullName() +
                        " changed password for user " + user.getFullName()
        );

        System.out.println("Password updated successfully.");
    }

    public void updateUserFullName(Database database, User user, String newFullName) {
        if (user == null) {
            System.out.println("User does not exist.");
            return;
        }

        if (newFullName == null || newFullName.isBlank()) {
            System.out.println("New full name cannot be empty.");
            return;
        }

        String oldFullName = user.getFullName();
        user.setFullName(newFullName);

        database.addLog(
                "ADMIN: " + getFullName() +
                        " changed full name from '" + oldFullName +
                        "' to '" + newFullName + "'"
        );

        System.out.println("Full name updated successfully.");
    }

    public void viewLogs(Database database) {
        database.printLogs();
    }

    @Override
    public void showMenu() {
        System.out.println("Admin menu");
    }
}