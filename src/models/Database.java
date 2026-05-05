package models;
import enums.RequestStatus;
import enums.RegistrationStatus;

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
    private List<Registration> registrations = new ArrayList<>();

    private Database() {}

    // create database
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    // Ensure all lists are initialized (for backward compatibility)
    private void ensureStorage() {
        if (users == null) {
            users = new ArrayList<>();
        }

        if (courses == null) {
            courses = new ArrayList<>();
        }

        if (researchProjects == null) {
            researchProjects = new ArrayList<>();
        }

        if (researchPapers == null) {
            researchPapers = new ArrayList<>();
        }

        if (logs == null) {
            logs = new ArrayList<>();
        }

        if (newsList == null) {
            newsList = new ArrayList<>();
        }

        if (employeeRequests == null) {
            employeeRequests = new ArrayList<>();
        }       

        if (registrations == null) {
            registrations = new ArrayList<>();
        }
    }

    // add log
    public void addLog(String action) {
        ensureStorage();
        logs.add(LocalDateTime.now() + " | " + action);
    }

    // get all logs 
    public void printLogs() {
        ensureStorage();

        if (logs.isEmpty()) {
            System.out.println("No logs yet.");
            return;
        }

        System.out.println("System logs:");
        System.out.println("----------------------------------");

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
        ensureStorage();

        if (user == null) {
            System.out.println("User does not exist.");
            return;
        }

        if (findUserByUsername(user.getUsername()) != null) {
            System.out.println("User with this username already exists.");
            addLog("DATABASE: failed to add user because username already exists: " + user.getUsername());
            return;
        }

        users.add(user);

        addLog("DATABASE: added user " + user.getFullName() + " (" + user.getUsername() + ")");
    }

    public void removeUser(User user) {
        ensureStorage();

        if (user == null) {
            System.out.println("User does not exist.");
            return;
        }

        if (users.remove(user)) {
            addLog("DATABASE: removed user " + user.getFullName() + " (" + user.getUsername() + ")");
        } else {
            System.out.println("User was not found in database.");
        }
    }

    public User findUserByUsername(String username) {
        ensureStorage();
        
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
        ensureStorage();

        if (course == null) {
            System.out.println("Course does not exist.");
            return;
        }

        if (findCourseByCode(course.getCode()) != null) {
            System.out.println("Course with this code already exists.");
            addLog("DATABASE: failed to add course because code already exists: " + course.getCode());
            return;
        }

        courses.add(course);

        addLog("DATABASE: added course " + course.getCode() + " - " + course.getName());
    }

    public Course findCourseByCode(String code) {
        ensureStorage();

        if (code == null || code.isBlank()) {
            return null;
        }

        for (Course course : courses) {
            if (course.getCode().equalsIgnoreCase(code)) {
                return course;
            }
        }

        return null;
    }

    public void addResearchProject(ResearchProject project) {
        ensureStorage();

        if (project == null) {
            System.out.println("Research project does not exist.");
            return;
        }

        if (findResearchProjectByTopic(project.getTopic()) != null) {
            System.out.println("Research project with this topic already exists.");
            addLog("DATABASE: failed to add research project because topic already exists: " + project.getTopic());
            return;
        }

        researchProjects.add(project);

        addLog("DATABASE: added research project '" + project.getTopic() + "'");
    }

    public ResearchProject findResearchProjectByTopic(String topic) {
        ensureStorage();

        if (topic == null || topic.isBlank()) {
            return null;
        }

        for (ResearchProject project : researchProjects) {
            if (project.getTopic().equalsIgnoreCase(topic)) {
                return project;
            }
        }

        return null;
    }

    public void addResearchPaper(ResearchPaper paper) {
        ensureStorage();

        if (paper == null) {
            System.out.println("Research paper does not exist.");
            return;
        }

        if (findResearchPaperByDoi(paper.getDoi()) != null) {
            System.out.println("Research paper with this DOI already exists.");
            addLog("DATABASE: failed to add research paper because DOI already exists: " + paper.getDoi());
            return;
        }

        researchPapers.add(paper);

        addLog("DATABASE: added research paper '" + paper.getTitle() + "'");
    }

    public ResearchPaper findResearchPaperByDoi(String doi) {
        ensureStorage();

        if (doi == null || doi.isBlank()) {
            return null;
        }

        for (ResearchPaper paper : researchPapers) {
            if (paper.getDoi().equalsIgnoreCase(doi)) {
                return paper;
            }
        }

        return null;
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
        int maxCitations = 0;

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

        if (researcher == null) {
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
        ensureStorage();
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

    public void addRegistration(Registration registration) {
        ensureStorage();

        if (registration == null) {
            System.out.println("Registration does not exist.");
            return;
        }

        if (!registrations.contains(registration)) {
            registrations.add(registration);
            addLog(
                "DATABASE: added registration request from " +
                        registration.getStudent().getFullName() +
                        " for course " + registration.getCourse().getName()
            );
        }
    }

    public List<Registration> getRegistrations() {
        ensureStorage();
        return registrations;
    }

    public List<Registration> getPendingRegistrations() {
        ensureStorage();

        List<Registration> pending = new ArrayList<>();

        for (Registration registration : registrations) {
            if (registration.getStatus() == RegistrationStatus.PENDING) {
                pending.add(registration);
            }
        }

        return pending;
    }

    public void printPendingRegistrations() {
        List<Registration> pending = getPendingRegistrations();

        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        System.out.println("Pending registrations:");
        System.out.println("--------------------------------");

        for (int i = 0; i < pending.size(); i++) {
            System.out.println((i + 1) + ". " + pending.get(i));
        }
    }

    public List<User> getUsers() {
        ensureStorage();
        return users;
    }

    public List<Course> getCourses() {
        ensureStorage();
        return courses;
    }

    public List<ResearchProject> getResearchProjects() {
        ensureStorage();
        return researchProjects;
    }

    public List<ResearchPaper> getResearchPapers() {
        ensureStorage();
        return researchPapers;
    }

    public void printDatabaseSummary() {
        ensureStorage();

        System.out.println("=== Database Summary ===");
        System.out.println("Users: " + users.size());
        System.out.println("Courses: " + courses.size());
        System.out.println("Registrations: " + registrations.size());
        System.out.println("Research papers: " + researchPapers.size());
        System.out.println("Research projects: " + researchProjects.size());
        System.out.println("News: " + newsList.size());
        System.out.println("Employee requests: " + employeeRequests.size());
        System.out.println("Logs: " + logs.size());
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