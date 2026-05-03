package models;
import enums.RequestStatus;

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
    private List<News> newsList = new ArrayList<>();
    private List<EmployeeRequest> employeeRequests = new ArrayList<>();

    private Database() {}

    // create database
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    // add log
    public void addLog(String action) {
        logs.add(LocalDateTime.now() + " | " + action);
    }

    // get all logs 
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

    public User findUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        return null;
    }

    public boolean isUsernameTaken(String username) {
        return findUserByUsername(username) != null;
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

    // get researcher's school (major/department)
    private String getResearcherSchool(User user) {
        if (user instanceof Student) {
            return ((Student) user).getMajor();
        }

        if (user instanceof Employee) {
            return ((Employee) user).getDepartment();
        }

        return "Unknown";
    }

    // Get top cited researcher by it's school (major/department)
    public Researcher getTopCitedResearcherBySchool(String school) {
        Researcher topResearcher = null;
        int maxCitations = -1;

        for (User user : users) {
            if (user instanceof Researcher) {
                String researcherSchool = getResearcherSchool(user);

                if (researcherSchool != null && researcherSchool.equalsIgnoreCase(school)) {
                    Researcher researcher = (Researcher) user;
                    int citations = researcher.getTotalCitations();

                    if (citations > maxCitations) {
                        maxCitations = citations;
                        topResearcher = researcher;
                    }
                }
            }
        }

        return topResearcher;
    }

    // Print top cited researcher by it's school (major/department)
    public void printTopCitedResearcherBySchool(String school) {
        Researcher researcher = getTopCitedResearcherBySchool(school);

        if (researcher == null) {
            System.out.println("No researchers found for school/department: " + school);
            return;
        }

        User user = (User) researcher;

        System.out.println("Top cited researcher of " + school + ":");
        System.out.println(user.getFullName());
        System.out.println("Total citations: " + researcher.getTotalCitations());
        System.out.println("H-index: " + researcher.getHIndex());
    }

    // Get top cited researcher by year
    public Researcher getTopCitedResearcherByYear(int year) {
        Researcher topResearcher = null;
        int maxCitations = -1;

        for (User user : users) {
            if (user instanceof Researcher) {
                Researcher researcher = (Researcher) user;
                int citations = researcher.getCitationsByYear(year);

                if (citations > maxCitations) {
                    maxCitations = citations;
                    topResearcher = researcher;
                }
            }
        }

        return topResearcher;
    }

    // Print top cited researcher by year
    public void printTopCitedResearcherByYear(int year) {
        Researcher researcher = getTopCitedResearcherByYear(year);

        if (researcher == null || researcher.getCitationsByYear(year) == 0) {
            System.out.println("No cited researchers found for year: " + year);
            return;
        }

        User user = (User) researcher;

        System.out.println("Top cited researcher of year " + year + ":");
        System.out.println(user.getFullName());
        System.out.println("Citations in " + year + ": " + researcher.getCitationsByYear(year));
        System.out.println("H-index: " + researcher.getHIndex());
    }

    // Get top cited researcher by year + school
    public Researcher getTopCitedResearcherBySchoolAndYear(String school, int year) {
        Researcher topResearcher = null;
        int maxCitations = -1;

        for (User user : users) {
            if (user instanceof Researcher) {
                String researcherSchool = getResearcherSchool(user);

                if (researcherSchool != null && researcherSchool.equalsIgnoreCase(school)) {
                    Researcher researcher = (Researcher) user;
                    int citations = researcher.getCitationsByYear(year);

                    if (citations > maxCitations) {
                        maxCitations = citations;
                        topResearcher = researcher;
                    }
                }
            }
        }

        return topResearcher;
    }

    // Print top cited researcher by year + school
    public void printTopCitedResearcherBySchoolAndYear(String school, int year) {
        Researcher researcher = getTopCitedResearcherBySchoolAndYear(school, year);

        if (researcher == null || researcher.getCitationsByYear(year) == 0) {
            System.out.println("No cited researchers found for " + school + " in " + year);
            return;
        }

        User user = (User) researcher;

        System.out.println("Top cited researcher of " + school + " in " + year + ":");
        System.out.println(user.getFullName());
        System.out.println("Citations in " + year + ": " + researcher.getCitationsByYear(year));
        System.out.println("H-index: " + researcher.getHIndex());
    }

    // get top cited researcher of all time
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

    // print top cited researcher of all time 
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

    // print all researches
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

    // to publish news
    public void publishNews(News news) {
        newsList.add(news);

        for (User user : users) {
            user.receiveNews(news);
        }

        addLog("NEWS: published news '" + news.getTitle() + "' by " + news.getAuthor().getFullName());
    }

    public List<News> getNewsList() {
        return newsList;
    }

    // Print all news 
    public void printAllNews() {
        if (newsList.isEmpty()) {
            System.out.println("No news published yet.");
            return;
        }

        System.out.println("All university news:");
        System.out.println("--------------------------------");

        for (News news : newsList) {
            System.out.println(news);
            System.out.println("--------------------------------");
        }
    }

    // Ensure employee requests list is initialized
    private void ensureEmployeeRequests() {
        if (employeeRequests == null) {
            employeeRequests = new ArrayList<>();
        }
    }

    // Add employee request
    public void addEmployeeRequest(EmployeeRequest request) {
        ensureEmployeeRequests();

        if (request == null) {
            System.out.println("Request does not exist.");
            return;
        }

        if (!employeeRequests.contains(request)) {
            employeeRequests.add(request);
        }
    }

    // Get employee requests
    public List<EmployeeRequest> getEmployeeRequests() {
        ensureEmployeeRequests();
        return employeeRequests;
    }

    // Get signed employee requests
    public List<EmployeeRequest> getSignedEmployeeRequests() {
        ensureEmployeeRequests();

        List<EmployeeRequest> signedRequests = new ArrayList<>();

        for (EmployeeRequest request : employeeRequests) {
            if (request.getStatus() == RequestStatus.SIGNED) {
                signedRequests.add(request);
            }
        }

        return signedRequests;
    }

    // Print all employee requests
    public void printAllEmployeeRequests() {
        ensureEmployeeRequests();

        if (employeeRequests.isEmpty()) {
            System.out.println("No employee requests found.");
            return;
        }

        System.out.println("All employee requests:");
        System.out.println("--------------------------------");

        for (EmployeeRequest request : employeeRequests) {
            System.out.println(request);
            System.out.println("--------------------------------");
        }
    }

    // Print signed employee requests
    public void printSignedEmployeeRequests() {
        List<EmployeeRequest> signedRequests = getSignedEmployeeRequests();

        if (signedRequests.isEmpty()) {
            System.out.println("No signed employee requests found.");
            return;
        }

        System.out.println("Signed employee requests:");
        System.out.println("--------------------------------");

        for (EmployeeRequest request : signedRequests) {
            System.out.println(request);
            System.out.println("--------------------------------");
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