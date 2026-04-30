package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.*;
import java.time.LocalDateTime;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Database instance;

    private List<User> users = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<ResearchProject> researchProjects = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private List<String> logs = new ArrayList<>();

    private Database() {}

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    public void addLog(String action) {
        logs.add(LocalDateTime.now() + " | " + action);
    }

    public void printLogs() {
        if (logs.isEmpty()) {
            System.out.println("No logs yet.");
            return;
        }

        System.out.println("System logs:");
        for (String log : logs) {
            System.out.println(log);
        }
    }

    // authentification for users
    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.login(username, password)) {
                addLog("LOGIN SUCCESS: " + user.getFullName() + " logged in.");
                return user;
            }
        }

        addLog("LOGIN FAILED: username = " + username);
        return null;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void addResearchProject(ResearchProject project) {
        researchProjects.add(project);
    }

    public void addResearchPaper(ResearchPaper paper) {
        researchPapers.add(paper);
    }

    public void printAllResearchPapers(Comparator<ResearchPaper> comparator) {
        researchPapers.stream().sorted(comparator).forEach(System.out::println);
    }

    public Researcher getTopCitedResearcher() {
        Researcher topResearcher = null;
        int maxCitations = -1;

        for (User user: users) {
            if (user instanceof Researcher researcher) {
                int totalCitations = researcher.getTotalCitations();

                if (totalCitations > maxCitations) {
                    maxCitations = totalCitations;
                    topResearcher = researcher;
                }
            }
        }

        return topResearcher;
    }

    public void printTopCitedResearcher() {
        Researcher topResearcher = getTopCitedResearcher();

        if (topResearcher == null) {
            System.out.println("No researchers found.");
            return;
        }

        User user = (User) topResearcher;

        System.out.println("Top cited researcher:");
        System.out.println(user.getFullName());
        System.out.println("Total citations: " + topResearcher.getTotalCitations());
        System.out.println("H-index: " + topResearcher.getHIndex());
    }

    public void printAllResearchers() {
        System.out.println("All researchers:");

        for (User user : users) {
            if (user instanceof Researcher researcher) {
                System.out.println(user.getFullName() +
                        " | h-index: " + researcher.getHIndex() +
                        " | citations: " + researcher.getTotalCitations());
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<ResearchProject> getResearchProjects() {
        return researchProjects;
    }

    public List<ResearchPaper> getResearchPapers() {
        return researchPapers;
    }
    
    // Save data to database
    public void save(String fileName) {
        addLog("DATABASE: database saved to file " + fileName);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
            System.out.println("Database saved successfully.");
        } catch (IOException e) {
            System.out.println("Error while saving database: " + e.getMessage());
        }
    }

    // Load data from database
    public static Database load(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            instance = (Database) in.readObject();
            instance.addLog("DATABASE: database loaded from file " + fileName);
            System.out.println("Database loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load database. New database was created.");
            instance = new Database();
            instance.addLog("DATABASE: new database was created because loading failed.");
        }

        return instance;
    }

}