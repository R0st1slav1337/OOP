import enums.*;
import models.*;
import exceptions.*;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Demo {
    private static final String DATA_FILE = "university.ser";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Database database = Database.load(DATA_FILE);

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== Research-Oriented University System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    login(database);
                    break;
                case 2:
                    User newUser = register(database);

                    if (newUser != null) {
                        System.out.println("Registration successful. Logged in as: " + newUser.getFullName());
                        openUserMenu(database, newUser);
                    }

                    break;
                case 0:
                    database.save(DATA_FILE);
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    private static void login(Database database) {
        System.out.println();
        System.out.println("=== Login ===");

        System.out.print("Username: ");
        String username = readLine();

        System.out.print("Password: ");
        String password = readLine();

        User user = database.authenticate(username, password);

        if (user == null) {
            System.out.println("Wrong username or password.");
            return;
        }

        System.out.println("Logged in as: " + user.getFullName());
        openUserMenu(database, user);
    }

    private static User register(Database database) {
        System.out.println();
        System.out.println("=== Register ===");

        System.out.print("Username: ");
        String username = readLine();

        if (database.isUsernameTaken(username)) {
            System.out.println("Username is already taken.");
            return null;
        }

        System.out.print("Password: ");
        String password = readLine();

        System.out.print("Full name: ");
        String fullName = readLine();

        UserType userType = readEnum(UserType.class, "Choose user type");

        String id = userType.name().charAt(0) + String.valueOf(System.currentTimeMillis());

        User user = null;

        switch (userType) {
            case STUDENT:
                System.out.print("Year: ");
                int year = readInt();

                System.out.print("Major: ");
                String major = readLine();

                System.out.print("H-index: ");
                int studentHIndex = readInt();

                user = UserFactory.createStudent(
                        id,
                        username,
                        password,
                        fullName,
                        year,
                        major,
                        studentHIndex
                );
                break;

            case TEACHER:
                System.out.print("Salary: ");
                double teacherSalary = readDouble();

                System.out.print("Department: ");
                String teacherDepartment = readLine();

                TeacherTitle title = readEnum(TeacherTitle.class, "Choose teacher title");

                System.out.print("H-index: ");
                int teacherHIndex = readInt();

                user = UserFactory.createTeacher(
                        id,
                        username,
                        password,
                        fullName,
                        teacherSalary,
                        teacherDepartment,
                        title,
                        teacherHIndex
                );
                break;

            case MANAGER:
                System.out.print("Salary: ");
                double managerSalary = readDouble();

                System.out.print("Department: ");
                String managerDepartment = readLine();

                ManagerType managerType = readEnum(ManagerType.class, "Choose manager type");

                user = UserFactory.createManager(
                        id,
                        username,
                        password,
                        fullName,
                        managerSalary,
                        managerDepartment,
                        managerType
                );
                break;

            case ADMIN:
                System.out.print("Salary: ");
                double adminSalary = readDouble();

                System.out.print("Department: ");
                String adminDepartment = readLine();

                user = UserFactory.createAdmin(
                        id,
                        username,
                        password,
                        fullName,
                        adminSalary,
                        adminDepartment
                );
                break;

            default:
                System.out.println("Unknown user type.");
                return null;
        }

        database.addUser(user);

        return user;
    }

    private static void openUserMenu(Database database, User user) {
        if (user instanceof Student) {
            studentMenu(database, (Student) user);
        } else if (user instanceof Admin) {
            adminMenu(database, (Admin) user);
        } else if (user instanceof Manager) {
            managerMenu(database, (Manager) user);
        } else if (user instanceof Teacher) {
            teacherMenu(database, (Teacher) user);
        } else {
            System.out.println("Unknown user role.");
        }
    }

    private static void studentMenu(Database database, Student student) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Student Menu: " + student.getFullName() + " ===");
            System.out.println("1. View my courses");
            System.out.println("2. Request course registration");
            System.out.println("3. View marks");
            System.out.println("4. View transcript");
            System.out.println("5. View schedule");
            System.out.println("6. Rate teacher");
            System.out.println("7. View news");
            System.out.println("8. Research menu");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    student.viewCourses();
                    break;
                case 2:
                    requestCourseRegistration(database, student);
                    break;
                case 3:
                    student.viewMarks();
                    break;
                case 4:
                    student.getTranscript().printTranscript();
                    break;
                case 5:
                    student.viewSchedule();
                    break;
                case 6:
                    rateTeacher(database, student);
                    break;
                case 7:
                    student.viewNews();
                    break;
                case 8:
                    researchMenu(database, student, student);
                    break;
                case 0:
                    System.out.println("5. Create lesson");
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    private static void teacherMenu(Database database, Teacher teacher) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Teacher Menu: " + teacher.getFullName() + " ===");
            System.out.println("1. View my courses");
            System.out.println("2. Put mark");
            System.out.println("3. View schedule");
            System.out.println("4. View ratings");
            System.out.println("5. Create lesson");
            System.out.println("6. Mark attendance");
            System.out.println("7. View inbox");
            System.out.println("8. Send message to employee");
            System.out.println("9. Send complaint");
            System.out.println("10. Create employee request");
            System.out.println("11. View news");
            System.out.println("12. Research menu");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    teacher.viewCourses();
                    break;
                case 2:
                    putMark(database, teacher);
                    break;
                case 3:
                    teacher.viewSchedule();
                    break;
                case 4:
                    teacher.printRatings();
                    break;
                case 5:
                    createLesson(database, teacher);
                    break;
                case 6:
                    markAttendance(database, teacher);
                    break;
                case 7:
                    teacher.viewInbox();
                    break;
                case 8:
                    sendEmployeeMessage(database, teacher);
                    break;
                case 9:
                    sendEmployeeComplaint(database, teacher);
                    break;
                case 10:
                    createEmployeeRequest(teacher);
                    break;
                case 11:
                    teacher.viewNews();
                    break;
                case 12:
                    researchMenu(database, teacher, teacher);
                    break;
                case 0:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    private static void managerMenu(Database database, Manager manager) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Manager Menu: " + manager.getFullName() + " ===");
            System.out.println("1. View pending registrations");
            System.out.println("2. Approve registration");
            System.out.println("3. Reject registration");
            System.out.println("4. Add course");
            System.out.println("5. Assign teacher to course");
            System.out.println("6. Generate overall report");
            System.out.println("7. Create news");
            System.out.println("8. View signed employee requests");
            System.out.println("9. Approve signed employee request");
            System.out.println("10. View inbox");
            System.out.println("11. Send message to employee");
            System.out.println("12. Generate course report");
            System.out.println("13. Show top cited researcher");
            System.out.println("14. Show top cited researcher by year");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    database.printPendingRegistrations();
                    break;
                case 2:
                    processRegistration(database, manager, true);
                    break;
                case 3:
                    processRegistration(database, manager, false);
                    break;
                case 4:
                    addCourse(database);
                    break;
                case 5:
                    assignTeacher(database, manager);
                    break;
                case 6:
                    manager.generateReport(new OverallPerformanceReport());
                    break;
                case 7:
                    createNews(manager);
                    break;
                case 8:
                    manager.viewSignedRequests();
                    break;
                case 9:
                    approveEmployeeRequest(database, manager);
                    break;
                case 10:
                    manager.viewInbox();
                    break;
                case 11:
                    sendEmployeeMessage(database, manager);
                    break;
                case 12:
                    generateCourseReport(database, manager);
                    break;
                case 13:
                    database.printTopCitedResearcher();
                    break;
                case 14:
                    printTopCitedResearcherByYear(database);
                    break;
                case 0:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    private static void adminMenu(Database database, Admin admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Admin Menu: " + admin.getFullName() + " ===");
            System.out.println("1. View users");
            System.out.println("2. Update username");
            System.out.println("3. Update password");
            System.out.println("4. Update full name");
            System.out.println("5. View logs");
            System.out.println("6. View inbox");
            System.out.println("7. Send message to employee");
            System.out.println("8. Set employee signing role");
            System.out.println("9. Sign employee request");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    admin.viewUsers(database);
                    break;
                case 2:
                    updateUsername(database, admin);
                    break;
                case 3:
                    updatePassword(database, admin);
                    break;
                case 4:
                    updateFullName(database, admin);
                    break;
                case 5:
                    admin.viewLogs(database);
                    break;
                case 6:
                    admin.viewInbox();
                    break;
                case 7:
                    sendEmployeeMessage(database, admin);
                    break;
                case 8:
                    setSigningRole(database);
                    break;
                case 9:
                    signEmployeeRequest(database, admin);
                    break;
                case 0:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    private static void researchMenu(Database database, User user, Researcher researcher) {
        boolean inResearchMenu = true;

        while (inResearchMenu) {
            System.out.println();
            System.out.println("=== Research Menu: " + user.getFullName() + " ===");
            System.out.println("1. Add research paper");
            System.out.println("2. Print my papers by date");
            System.out.println("3. Print my papers by citations");
            System.out.println("4. Print my papers by pages");
            System.out.println("5. Create research project");
            System.out.println("6. Join research project");
            System.out.println("7. View my research projects");
            System.out.println("8. Show all university papers by citations");
            System.out.println("0. Back");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    addResearchPaper(database, user, researcher);
                    break;
                case 2:
                    researcher.printPapers(ResearchPaperComparators.BY_DATE);
                    break;
                case 3:
                    researcher.printPapers(ResearchPaperComparators.BY_CITATIONS);
                    break;
                case 4:
                    researcher.printPapers(ResearchPaperComparators.BY_PAGES);
                    break;
                case 5:
                    createResearchProject(database, user, researcher);
                    break;
                case 6:
                    joinResearchProject(database, user, researcher);
                    break;
                case 7:
                    viewResearchProjects(researcher);
                    break;
                case 8:
                    database.printAllResearchPapers(ResearchPaperComparators.BY_CITATIONS);
                    break;
                case 0:
                    inResearchMenu = false;
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    private static void requestCourseRegistration(Database database, Student student) {
        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        Registration registration = student.requestRegistration(course);

        if (registration != null) {
            database.addRegistration(registration);
            System.out.println("Registration request was created. Wait for manager approval.");
        }
    }

    private static void processRegistration(Database database, Manager manager, boolean approve) {
        database.printPendingRegistrations();

        java.util.List<Registration> pending = database.getPendingRegistrations();

        if (pending.isEmpty()) {
            return;
        }

        System.out.print("Choose request number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= pending.size()) {
            System.out.println("Wrong request number.");
            return;
        }

        Registration registration = pending.get(index);
        manager.addRegistration(registration);

        if (approve) {
            manager.approveRegistration(registration);
        } else {
            manager.rejectRegistration(registration);
        }
    }

    private static void addCourse(Database database) {
        System.out.print("Course code: ");
        String code = readLine();

        if (database.findCourseByCode(code) != null) {
            System.out.println("Course with this code already exists.");
            return;
        }

        System.out.print("Course name: ");
        String name = readLine();

        System.out.print("Credits: ");
        int credits = readInt();

        System.out.print("Intended major (or ALL): ");
        String intendedMajor = readLine();

        System.out.print("Intended year (0 for ALL): ");
        int intendedYear = readInt();

        Course course = new Course(code, name, credits, intendedMajor, intendedYear);
        database.addCourse(course);

        System.out.println("Course added successfully.");
    }

    private static void assignTeacher(Database database, Manager manager) {
        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.print("Teacher username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Teacher)) {
            System.out.println("Teacher not found.");
            return;
        }

        manager.assignTeacher(course, (Teacher) user);

        System.out.println("Teacher assigned successfully.");
    }

    private static void putMark(Database database, Teacher teacher) {
        System.out.print("Student username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Student)) {
            System.out.println("Student not found.");
            return;
        }

        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.print("1st attestation: ");
        double first = readDouble();

        System.out.print("2nd attestation: ");
        double second = readDouble();

        System.out.print("Final exam: ");
        double finalExam = readDouble();

        try {
            Mark mark = new Mark(first, second, finalExam);
            teacher.putMark((Student) user, course, mark);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createLesson(Database database, Teacher teacher) {
        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.print("Lesson topic: ");
        String topic = readLine();

        LessonType type = readEnum(LessonType.class, "Choose lesson type");

        LocalDateTime dateTime = readDateTime();

        System.out.print("Room: ");
        String room = readLine();

        teacher.createLesson(course, topic, type, dateTime, room);
    }

    private static void markAttendance(Database database, Teacher teacher) {
        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        List<Lesson> lessons = course.getLessons();

        if (lessons == null || lessons.isEmpty()) {
            System.out.println("No lessons found for this course.");
            return;
        }

        System.out.println("Lessons:");
        for (int i = 0; i < lessons.size(); i++) {
            System.out.println((i + 1) + ". " + lessons.get(i));
        }

        System.out.print("Choose lesson number: ");
        int lessonIndex = readInt() - 1;

        if (lessonIndex < 0 || lessonIndex >= lessons.size()) {
            System.out.println("Wrong lesson number.");
            return;
        }

        Lesson lesson = lessons.get(lessonIndex);

        System.out.print("Student username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Student)) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Present? yes/no: ");
        String answer = readLine();

        boolean present = answer.equalsIgnoreCase("yes")
                || answer.equalsIgnoreCase("y")
                || answer.equalsIgnoreCase("true");

        teacher.markAttendance(lesson, (Student) user, present);

        System.out.println();
        lesson.printAttendance();
    }

    private static void rateTeacher(Database database, Student student) {
        System.out.print("Teacher username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Teacher)) {
            System.out.println("Teacher not found.");
            return;
        }

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.print("Rating 1-5: ");
        int rating = readInt();

        System.out.print("Comment: ");
        String comment = readLine();

        try {
            student.rateTeacher((Teacher) user, course, rating, comment);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addResearchPaper(Database database, User user, Researcher researcher) {
        System.out.print("Paper title: ");
        String title = readLine();

        System.out.print("Journal: ");
        String journal = readLine();

        System.out.print("Pages: ");
        int pages = readInt();

        System.out.print("Citations: ");
        int citations = readInt();

        LocalDate publicationDate = readDate();

        System.out.print("DOI: ");
        String doi = readLine();

        if (database.findResearchPaperByDoi(doi) != null) {
            System.out.println("Research paper with this DOI already exists.");
            return;
        }

        ResearchPaper paper = new ResearchPaper(
                title,
                List.of(user),
                journal,
                pages,
                citations,
                publicationDate,
                doi
        );

        researcher.addResearchPaper(paper);
        database.addResearchPaper(paper);

        System.out.println("Research paper added successfully.");
    }

    private static void createResearchProject(Database database, User user, Researcher researcher) {
        System.out.print("Project topic: ");
        String topic = readLine();

        if (database.findResearchProjectByTopic(topic) != null) {
            System.out.println("Research project with this topic already exists.");
            return;
        }

        ResearchProject project = new ResearchProject(topic);
        database.addResearchProject(project);

        try {
            researcher.joinProject(project);
            System.out.println("Research project created and joined successfully.");
        } catch (NotResearcherException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void joinResearchProject(Database database, User user, Researcher researcher) {
        List<ResearchProject> projects = database.getResearchProjects();

        if (projects == null || projects.isEmpty()) {
            System.out.println("No research projects found.");
            return;
        }

        System.out.println("Research projects:");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getTopic());
        }

        System.out.print("Choose project number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= projects.size()) {
            System.out.println("Wrong project number.");
            return;
        }

        try {
            researcher.joinProject(projects.get(index));
            System.out.println("Joined research project successfully.");
        } catch (NotResearcherException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewResearchProjects(Researcher researcher) {
        List<ResearchProject> projects = researcher.getResearchProjects();

        if (projects == null || projects.isEmpty()) {
            System.out.println("No research projects found.");
            return;
        }

        System.out.println("My research projects:");
        System.out.println("--------------------------------");

        for (ResearchProject project : projects) {
            System.out.println(project);
        }
    }

    private static void createNews(Manager manager) {
        System.out.print("News title: ");
        String title = readLine();

        System.out.print("News content: ");
        String content = readLine();

        manager.createNews(title, content);
    }

    private static void generateCourseReport(Database database, Manager manager) {
        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        manager.generateReport(new CoursePerformanceReport(course));
    }

    private static void printTopCitedResearcherByYear(Database database) {
        System.out.print("Year: ");
        int year = readInt();

        database.printTopCitedResearcherByYear(year);
    }

    private static void sendEmployeeMessage(Database database, Employee sender) {
        System.out.print("Recipient username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Employee)) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Message text: ");
        String text = readLine();

        sender.sendMessage((Employee) user, text);
    }

    private static void sendEmployeeComplaint(Database database, Employee sender) {
        System.out.print("Recipient username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Employee)) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Complaint subject: ");
        String subject = readLine();

        System.out.print("Complaint description: ");
        String description = readLine();

        sender.sendComplaint((Employee) user, subject, description);
    }

    private static void createEmployeeRequest(Employee employee) {
        System.out.print("Request title: ");
        String title = readLine();

        System.out.print("Request description: ");
        String description = readLine();

        employee.createRequest(title, description);
    }

    private static void approveEmployeeRequest(Database database, Manager manager) {
        java.util.List<EmployeeRequest> signedRequests = database.getSignedEmployeeRequests();

        if (signedRequests.isEmpty()) {
            System.out.println("No signed employee requests found.");
            return;
        }

        for (int i = 0; i < signedRequests.size(); i++) {
            System.out.println((i + 1) + ". " + signedRequests.get(i).getTitle() +
                    " from " + signedRequests.get(i).getSender().getFullName());
        }

        System.out.print("Choose request number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= signedRequests.size()) {
            System.out.println("Wrong request number.");
            return;
        }

        manager.approveRequest(signedRequests.get(index));
    }

    private static void signEmployeeRequest(Database database, Employee signer) {
        java.util.List<EmployeeRequest> requests = database.getEmployeeRequests();

        if (requests.isEmpty()) {
            System.out.println("No employee requests found.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            EmployeeRequest request = requests.get(i);
            System.out.println((i + 1) + ". " + request.getTitle() +
                    " | Status: " + request.getStatus() +
                    " | From: " + request.getSender().getFullName());
        }

        System.out.print("Choose request number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= requests.size()) {
            System.out.println("Wrong request number.");
            return;
        }

        signer.signRequest(requests.get(index));
    }

    private static void setSigningRole(Database database) {
        System.out.print("Employee username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Employee)) {
            System.out.println("Employee not found.");
            return;
        }

        RequestSignerRole role = readEnum(RequestSignerRole.class, "Choose signing role");

        ((Employee) user).setSigningRole(role);
        System.out.println("Signing role was assigned.");
    }

    private static void updateUsername(Database database, Admin admin) {
        System.out.print("Current username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("New username: ");
        String newUsername = readLine();

        admin.updateUserUsername(database, user, newUsername);
    }

    private static void updatePassword(Database database, Admin admin) {
        System.out.print("Username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("New password: ");
        String newPassword = readLine();

        admin.updateUserPassword(database, user, newPassword);
    }

    private static void updateFullName(Database database, Admin admin) {
        System.out.print("Username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("New full name: ");
        String newFullName = readLine();

        admin.updateUserFullName(database, user, newFullName);
    }

    private static void printCourses(Database database) {
        java.util.List<Course> courses = database.getCourses();

        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        System.out.println("Available courses:");
        System.out.println("--------------------------------");

        for (Course course : courses) {
            System.out.println(course);
        }
    }

    private static String readLine() {
        return scanner.nextLine().trim();
    }

    private static int readInt() {
        while (true) {
            try {
                String input = readLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid integer: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                String input = readLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private static LocalDate readDate() {
        while (true) {
            try {
                System.out.print("Date yyyy-MM-dd: ");
                String input = readLine();
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Wrong date format. Example: 2026-05-01");
            }
        }
    }

    private static LocalDateTime readDateTime() {
        while (true) {
            try {
                System.out.print("Date/time yyyy-MM-ddTHH:mm, example 2026-05-01T10:00: ");
                String input = readLine();
                return LocalDateTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Wrong date/time format. Example: 2026-05-01T10:00");
            }
        }
    }

    private static <T extends Enum<T>> T readEnum(Class<T> enumClass, String title) {
        while (true) {
            System.out.println(title + ":");

            T[] values = enumClass.getEnumConstants();

            for (int i = 0; i < values.length; i++) {
                System.out.println((i + 1) + ". " + values[i]);
            }

            System.out.print("Choose option: ");
            int index = readInt() - 1;

            if (index >= 0 && index < values.length) {
                return values[index];
            }

            System.out.println("Wrong option.");
        }
    }
}