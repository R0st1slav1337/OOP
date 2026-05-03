package models;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Serializable, NewsObserver {
    protected String id;
    protected String username;
    protected String password;
    protected String fullName;

    private List<News> newsInbox = new ArrayList<>();

    public User() {}

    public User(String id, String username, String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public abstract void showMenu();

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            System.out.println("Password cannot be empty.");
            return;
        }
        
        this.password = password;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            System.out.println("Full name cannot be empty.");
            return;
        }

        this.fullName = fullName;
    }

    @Override
    public void receiveNews(News news) {
        if (newsInbox == null) {
            newsInbox = new ArrayList<>();
        }

        newsInbox.add(news);
    }

    public void viewNews() {
        if (newsInbox == null || newsInbox.isEmpty()) {
            System.out.println("No news available.");
            return;
        }

        System.out.println("News for " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (News news : newsInbox) {
            System.out.println(news);
            System.out.println("--------------------------------");
        }
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
