import enums.*;
import models.*;
import exceptions.*;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Demo {
    private static final String DB_DIR = "databases";
    private static String currentDataFile;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Database database = chooseDatabaseOnStart();

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
                    if (currentDataFile != null) {
                        database.save(currentDataFile);
                    } else {
                        System.out.println("No database file selected. Database was not saved.");
                    }
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Wrong option.");
            }
        }
    }

    // Login method that authenticates user
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

    // Register method with user type selection and specific fields for each type
    private static User register(Database database) {
        System.out.println();
        System.out.println("=== Register ===");

        User user = createUserFromInput(database);

        if (user == null) {
            return null;
        }

        database.addUser(user);
        return user;
    }

    // Helper method to create a user from input with validation and specific fields
    // for each user type
    private static User createUserFromInput(Database database) {
        System.out.print("Username: ");
        String username = readLine();

        if (username == null || username.isBlank()) {
            System.out.println("Username cannot be empty.");
            return null;
        }

        if (database.isUsernameTaken(username)) {
            System.out.println("Username is already taken.");
            return null;
        }

        System.out.print("Password: ");
        String password = readLine();

        if (password == null || password.isBlank()) {
            System.out.println("Password cannot be empty.");
            return null;
        }

        System.out.print("Full name: ");
        String fullName = readLine();

        if (fullName == null || fullName.isBlank()) {
            System.out.println("Full name cannot be empty.");
            return null;
        }

        UserType userType = readEnum(UserType.class, "Choose user type");

        String id = userType.name().charAt(0) + String.valueOf(System.currentTimeMillis());

        switch (userType) {
            case STUDENT:
                int year = readIntInRange("Year: ", 1, 4);

                String major = readMajorOrAll("Major: ");

                int studentHIndex = readIntInRange("H-index: ", 0, 1000);

                return UserFactory.createStudent(
                        id,
                        username,
                        password,
                        fullName,
                        year,
                        major,
                        studentHIndex);

            case TEACHER:
                double teacherSalary = readDoubleInRange("Salary: ", 0, 10000000);

                System.out.print("Department: ");
                String teacherDepartment = readLine();

                TeacherTitle title = readEnum(TeacherTitle.class, "Choose teacher title");

                int teacherHIndex = readIntInRange("H-index: ", 0, 1000);

                return UserFactory.createTeacher(
                        id,
                        username,
                        password,
                        fullName,
                        teacherSalary,
                        teacherDepartment,
                        title,
                        teacherHIndex);

            case MANAGER:
                double managerSalary = readDoubleInRange("Salary: ", 0, 100000000);

                System.out.print("Department: ");
                String managerDepartment = readLine();

                ManagerType managerType = readEnum(ManagerType.class, "Choose manager type");

                return UserFactory.createManager(
                        id,
                        username,
                        password,
                        fullName,
                        managerSalary,
                        managerDepartment,
                        managerType);

            case ADMIN:
                double adminSalary = readDoubleInRange("Salary: ", 0, 100000000);

                System.out.print("Department: ");
                String adminDepartment = readLine();

                return UserFactory.createAdmin(
                        id,
                        username,
                        password,
                        fullName,
                        adminSalary,
                        adminDepartment);

            case EMPLOYEE:
                double employeeSalary = readDoubleInRange("Salary: ", 0, 100000000);

                System.out.print("Department: ");
                String employeeDepartment = readLine();

                return UserFactory.createEmployee(
                        id,
                        username,
                        password,
                        fullName,
                        employeeSalary,
                        employeeDepartment);

            case RESEARCH_EMPLOYEE:
                double researchEmployeeSalary = readDoubleInRange("Salary: ", 0, 100000000);

                System.out.print("Department: ");
                String researchEmployeeDepartment = readLine();

                System.out.print("H-index: ");
                int researchEmployeeHIndex = readInt();

                return UserFactory.createResearchEmployee(
                        id,
                        username,
                        password,
                        fullName,
                        researchEmployeeSalary,
                        researchEmployeeDepartment,
                        researchEmployeeHIndex);

            default:
                System.out.println("Unknown user type.");
                return null;
        }
    }

    // User menu methods for different user types with specific options for each
    // type
    private static void openUserMenu(Database database, User user) {
        if (user instanceof Student) {
            studentMenu(database, (Student) user);
        } else if (user instanceof Admin) {
            adminMenu(database, (Admin) user);
        } else if (user instanceof Manager) {
            managerMenu(database, (Manager) user);
        } else if (user instanceof Teacher) {
            teacherMenu(database, (Teacher) user);
        } else if (user instanceof Employee) {
            employeeMenu(database, (Employee) user);
        } else {
            System.out.println("Unknown user role.");
        }
    }

    // Student menu
    private static void studentMenu(Database database, Student student) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Student Menu: " + student.getFullName() + " ===");
            System.out.println();
            System.out.println("--- Academic ---");
            System.out.println("1. View my courses");
            System.out.println("2. View available courses");
            System.out.println("3. View all courses with availability status");
            System.out.println("4. Request course registration");
            System.out.println("5. Drop course");
            System.out.println("6. View teachers for course");
            System.out.println();
            System.out.println("--- Marks & Attendance ---");
            System.out.println("7. View marks");
            System.out.println("8. View transcript");
            System.out.println("9. View schedule");
            System.out.println("10. View attendance");
            System.out.println();
            System.out.println("--- Feedback & Info ---");
            System.out.println("11. Rate teacher");
            System.out.println("12. View course details");
            System.out.println("13. View news");
            System.out.println("14. View my profile");
            System.out.println();
            System.out.println("--- Research ---");
            System.out.println("15. Research menu");
            System.out.println();
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    student.viewCourses();
                    break;
                case 2:
                    student.viewAvailableCourses(database);
                    break;
                case 3:
                    student.viewCoursesWithRegistrationStatus(database);
                    break;
                case 4:
                    requestCourseRegistration(database, student);
                    break;
                case 5:
                    dropCourse(database, student);
                    break;
                case 6:
                    viewTeachersForCourse(database, student);
                    break;
                case 7:
                    student.viewMarks();
                    break;
                case 8:
                    student.getTranscript().printTranscript();
                    break;
                case 9:
                    student.viewSchedule();
                    break;
                case 10:
                    student.viewAttendance();
                    break;
                case 11:
                    rateTeacher(database, student);
                    break;
                case 12:
                    viewCourseDetails(database);
                    break;
                case 13:
                    student.viewNews();
                    break;
                case 14:
                    student.printStudentDetails();
                    break;
                case 15:
                    researchMenu(database, student, student);
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

    // Teacher menu
    private static void teacherMenu(Database database, Teacher teacher) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Teacher Menu: " + teacher.getFullName() + " ===");
            System.out.println();
            System.out.println("--- Courses & Students ---");
            System.out.println("1. View my courses");
            System.out.println("2. View students for course");
            System.out.println("3. View course details");
            System.out.println();
            System.out.println("--- Marks & Lessons ---");
            System.out.println("4. Put mark");
            System.out.println("5. Create lesson");
            System.out.println("6. Mark attendance");
            System.out.println("7. View schedule");
            System.out.println("8. View attendance report");
            System.out.println();
            System.out.println("--- Feedback & Profile ---");
            System.out.println("9. View ratings");
            System.out.println("10. View news");
            System.out.println("11. View my profile");
            System.out.println();
            System.out.println("--- Communication ---");
            System.out.println("12. View inbox");
            System.out.println("13. View sent messages");
            System.out.println("14. Send message to employee");
            System.out.println("15. Send complaint");
            System.out.println("16. View sent complaints");
            System.out.println();
            System.out.println("--- Requests ---");
            System.out.println("17. Create employee request");
            System.out.println("18. View sent requests");
            System.out.println();
            System.out.println("--- Research ---");
            System.out.println("19. Research menu");
            System.out.println();
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    teacher.viewCourses();
                    break;
                case 2:
                    viewStudentsForCourse(database, teacher);
                    break;
                case 3:
                    viewCourseDetails(database);
                    break;
                case 4:
                    putMark(database, teacher);
                    break;
                case 5:
                    createLesson(database, teacher);
                    break;
                case 6:
                    markAttendance(database, teacher);
                    break;
                case 7:
                    teacher.viewSchedule();
                    break;
                case 8:
                    viewAttendanceReport(database, teacher);
                    break;
                case 9:
                    teacher.printRatings();
                    break;
                case 10:
                    teacher.viewNews();
                    break;
                case 11:
                    teacher.printTeacherDetails();
                    break;
                case 12:
                    teacher.viewInbox();
                    break;
                case 13:
                    teacher.viewSentMessages();
                    break;
                case 14:
                    sendEmployeeMessage(database, teacher);
                    break;
                case 15:
                    sendEmployeeComplaint(database, teacher);
                    break;
                case 16:
                    teacher.viewSentComplaints();
                    break;
                case 17:
                    createEmployeeRequest(teacher);
                    break;
                case 18:
                    teacher.viewSentRequests();
                    break;
                case 19:
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

    // Manager menu
    private static void managerMenu(Database database, Manager manager) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Manager Menu: " + manager.getFullName() + " ===");
            System.out.println();
            System.out.println("--- Course Registration ---");
            System.out.println("1. View pending registrations");
            System.out.println("2. Approve registration");
            System.out.println("3. Reject registration");
            System.out.println();
            System.out.println("--- Courses & Teachers ---");
            System.out.println("4. Add course");
            System.out.println("5. Assign teacher to course");
            System.out.println("6. Add prerequisite to course");
            System.out.println("7. View course details");
            System.out.println();
            System.out.println("--- Reports & Analytics ---");
            System.out.println("8. Generate overall report");
            System.out.println("9. Generate course report");
            System.out.println("10. View students sorted by GPA");
            System.out.println("11. View students alphabetically");
            System.out.println("12. View teachers alphabetically");
            System.out.println();
            System.out.println("--- Research Analytics ---");
            System.out.println("13. Show top cited researcher");
            System.out.println("14. Show top cited researcher by year");
            System.out.println("15. Show top cited researcher by school");
            System.out.println("16. Show top cited researcher by school and year");
            System.out.println("17. Show all researchers");
            System.out.println();
            System.out.println("--- News ---");
            System.out.println("18. Create news");
            System.out.println();
            System.out.println("--- Employee Requests ---");
            System.out.println("19. View signed employee requests");
            System.out.println("20. Approve signed employee request");
            System.out.println("21. Reject signed employee request");
            System.out.println("22. View sent requests");
            System.out.println();
            System.out.println("--- Communication ---");
            System.out.println("23. View inbox");
            System.out.println("24. View sent messages");
            System.out.println("25. Send message to employee");
            System.out.println("26. Send complaint");
            System.out.println("27. View sent complaints");
            System.out.println();
            System.out.println("--- Search / Profile ---");
            System.out.println("28. View user details");
            System.out.println("29. View my profile");
            System.out.println();
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
                    addPrerequisiteToCourse(database);
                    break;
                case 7:
                    viewCourseDetails(database);
                    break;
                case 8:
                    manager.generateReport(new OverallPerformanceReport());
                    break;
                case 9:
                    generateCourseReport(database, manager);
                    break;
                case 10:
                    manager.viewStudentsSortedByGpa(database);
                    break;
                case 11:
                    manager.viewStudentsAlphabetically(database);
                    break;
                case 12:
                    manager.viewTeachersAlphabetically(database);
                    break;
                case 13:
                    database.printTopCitedResearcher();
                    break;
                case 14:
                    printTopCitedResearcherByYear(database);
                    break;
                case 15:
                    printTopCitedResearcherBySchool(database);
                    break;
                case 16:
                    printTopCitedResearcherBySchoolAndYear(database);
                    break;
                case 17:
                    database.printAllResearchers();
                    break;
                case 18:
                    createNews(manager);
                    break;
                case 19:
                    manager.viewSignedRequests();
                    break;
                case 20:
                    processEmployeeRequest(database, manager, true);
                    break;
                case 21:
                    processEmployeeRequest(database, manager, false);
                    break;
                case 22:
                    manager.viewSentRequests();
                    break;
                case 23:
                    manager.viewInbox();
                    break;
                case 24:
                    manager.viewSentMessages();
                    break;
                case 25:
                    sendEmployeeMessage(database, manager);
                    break;
                case 26:
                    sendEmployeeComplaint(database, manager);
                    break;
                case 27:
                    manager.viewSentComplaints();
                    break;
                case 28:
                    viewUserDetails(database);
                    break;
                case 29:
                    manager.printEmployeeDetails();
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

    // Admin menu
    private static void adminMenu(Database database, Admin admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Admin Menu: " + admin.getFullName() + " ===");
            System.out.println();
            System.out.println("--- User Management ---");
            System.out.println("1. View users");
            System.out.println("2. Add user");
            System.out.println("3. Remove user");
            System.out.println("4. Update username");
            System.out.println("5. Update password");
            System.out.println("6. Update full name");
            System.out.println();
            System.out.println("--- Logs & System ---");
            System.out.println("7. View logs");
            System.out.println("8. View all news");
            System.out.println("9. View database summary");
            System.out.println();
            System.out.println("--- Communication ---");
            System.out.println("10. View inbox");
            System.out.println("11. View sent messages");
            System.out.println("12. Send message to employee");
            System.out.println();
            System.out.println("--- Complaints ---");
            System.out.println("13. View received complaints");
            System.out.println("14. Resolve complaint");
            System.out.println("15. View sent complaints");
            System.out.println();
            System.out.println("--- Requests ---");
            System.out.println("16. View all employee requests");
            System.out.println("17. Set employee signing role");
            System.out.println("18. Sign employee request");
            System.out.println("19. View sent requests");
            System.out.println();
            System.out.println("--- Search / Profile ---");
            System.out.println("20. View user details");
            System.out.println("21. View course details");
            System.out.println("22. View my profile");
            System.out.println();
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    admin.viewUsers(database);
                    break;
                case 2:
                    adminAddUser(database, admin);
                    break;
                case 3:
                    removeUser(database, admin);
                    break;
                case 4:
                    updateUsername(database, admin);
                    break;
                case 5:
                    updatePassword(database, admin);
                    break;
                case 6:
                    updateFullName(database, admin);
                    break;
                case 7:
                    admin.viewLogs(database);
                    break;
                case 8:
                    database.printAllNews();
                    break;
                case 9:
                    database.printDatabaseSummary();
                    break;
                case 10:
                    admin.viewInbox();
                    break;
                case 11:
                    admin.viewSentMessages();
                    break;
                case 12:
                    sendEmployeeMessage(database, admin);
                    break;
                case 13:
                    admin.viewReceivedComplaints();
                    break;
                case 14:
                    resolveComplaint(admin);
                    break;
                case 15:
                    admin.viewSentComplaints();
                    break;
                case 16:
                    database.printAllEmployeeRequests();
                    break;
                case 17:
                    setSigningRole(database);
                    break;
                case 18:
                    signEmployeeRequest(database, admin);
                    break;
                case 19:
                    admin.viewSentRequests();
                    break;
                case 20:
                    viewUserDetails(database);
                    break;
                case 21:
                    viewCourseDetails(database);
                    break;
                case 22:
                    admin.printEmployeeDetails();
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

    // Employee menu
    private static void employeeMenu(Database database, Employee employee) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("=== Employee Menu: " + employee.getFullName() + " ===");
            System.out.println();
            System.out.println("--- Communication ---");
            System.out.println("1. View inbox");
            System.out.println("2. View sent messages");
            System.out.println("3. Send message to employee");
            System.out.println();
            System.out.println("--- Complaints ---");
            System.out.println("4. Send complaint");
            System.out.println("5. View sent complaints");
            System.out.println("6. View received complaints");
            System.out.println();
            System.out.println("--- Requests ---");
            System.out.println("7. Create employee request");
            System.out.println("8. View sent requests");
            System.out.println();
            System.out.println("--- Info ---");
            System.out.println("9. View news");
            System.out.println("10. View my profile");
            if (employee instanceof Researcher) {
                System.out.println();
                System.out.println("--- Research ---");
                System.out.println("11. Research menu");
            }
            System.out.println();
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    employee.viewInbox();
                    break;
                case 2:
                    employee.viewSentMessages();
                    break;
                case 3:
                    sendEmployeeMessage(database, employee);
                    break;
                case 4:
                    sendEmployeeComplaint(database, employee);
                    break;
                case 5:
                    employee.viewSentComplaints();
                    break;
                case 6:
                    employee.viewReceivedComplaints();
                    break;
                case 7:
                    createEmployeeRequest(employee);
                    break;
                case 8:
                    employee.viewSentRequests();
                    break;
                case 9:
                    employee.viewNews();
                    break;
                case 10:
                    employee.printEmployeeDetails();
                    break;
                case 11:
                    if (employee instanceof Researcher) {
                        researchMenu(database, employee, (Researcher) employee);
                    } else {
                        System.out.println("Wrong option.");
                    }
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

    // Research menu (additional menu)
    private static void researchMenu(Database database, User user, Researcher researcher) {
        boolean inResearchMenu = true;

        while (inResearchMenu) {
            System.out.println();
            System.out.println("=== Research Menu: " + user.getFullName() + " ===");
            System.out.println();
            System.out.println("--- My Papers ---");
            System.out.println("1. Add research paper");
            System.out.println("2. Print my papers by date");
            System.out.println("3. Print my papers by citations");
            System.out.println("4. Print my papers by pages");
            System.out.println();
            System.out.println("--- Research Projects ---");
            System.out.println("5. Create research project");
            System.out.println("6. Join research project");
            System.out.println("7. View my research projects");
            System.out.println("8. Add my paper to research project");
            System.out.println("9. View project papers");
            System.out.println();
            System.out.println("--- University Research ---");
            System.out.println("10. Show all university papers by citations");
            System.out.println();
            System.out.println("0. Back");
            System.out.print("Choose option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    addResearchPaper(database, user, researcher);
                    break;
                case 2:
                    printResearchPapers(researcher, ResearchPaperComparators.BY_DATE);
                    break;
                case 3:
                    printResearchPapers(researcher, ResearchPaperComparators.BY_CITATIONS);
                    break;
                case 4:
                    printResearchPapers(researcher, ResearchPaperComparators.BY_PAGES);
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
                    addPaperToResearchProject(database, user, researcher);
                    break;
                case 9:
                    viewProjectPapers(database);
                    break;
                case 10:
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

    // Helper method for viewing teachers for a course by student with course
    // selection and teachers display
    private static void viewTeachersForCourse(Database database, Student student) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        student.viewTeachersForCourse(course);
    }

    // Helper method for viewing students for a course by teacher with course
    // selection and students display
    private static void viewStudentsForCourse(Database database, Teacher teacher) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        teacher.viewStudentsForCourse(course);
    }

    // Helper method for viewing attendance report for a course by teacher with
    // course selection and attendance report display
    private static void viewAttendanceReport(Database database, Teacher teacher) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        teacher.viewAttendanceReport(course);
    }

    // Helper method for requesting course registration by student with course
    // selection and registration creation
    private static void requestCourseRegistration(Database database, Student student) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        Registration registration = student.requestRegistration(course);

        if (registration != null) {
            database.addRegistration(registration);
            System.out.println("Registration request was created. Wait for manager approval.");
        }
    }

    // Helper method to drop a course by student with course selection and dropping
    // the course
    private static void dropCourse(Database database, Student student) {
        System.out.println("Your courses:");
        student.viewCourses();

        System.out.print("Course code to drop: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        student.dropCourse(course);
    }

    // Helper method for processing registration requests by manager with pending
    // registrations display, selection, and approval/rejection
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

    // Helper method for adding a new course by manager with course details input
    // and course creation
    private static void addCourse(Database database) {
        String code = readRequiredLine("Course code: ");

        if (database.findCourseByCode(code) != null) {
            System.out.println("Course with this code already exists.");
            return;
        }

        String name = readRequiredLine("Course name: ");

        int credits = readIntInRange("Credits: ", 1, 6);

        String intendedMajor = readMajorOrAll("Intended major (or ALL): ");

        int intendedYear = readIntInRange("Intended year (0 for ALL): ", 0, 4);

        int capacity = readIntInRange("Capacity: ", 1, 150);

        Course course = new Course(code, name, credits, intendedMajor, intendedYear, capacity);
        database.addCourse(course);

        System.out.println("Course added successfully.");
    }

    // Helper method for assigning a teacher to a course by manager with course
    // selection, teacher selection, and assignment
    private static void assignTeacher(Database database, Manager manager) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        Teacher teacher = selectTeacher(database);

        if (teacher == null) {
            return;
        }

        manager.assignTeacher(course, teacher);

        System.out.println("Teacher assigned successfully.");
    }

    // Helper method for putting a mark for a student by teacher with student
    // selection, course selection, and mark input
    private static void putMark(Database database, Teacher teacher) {
        Student student = selectStudent(database);

        if (student == null) {
            return;
        }

        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        double first = readDoubleInRange("1st attestation: ", 0, 30);
        double second = readDoubleInRange("2nd attestation: ", 0, 30);
        double finalExam = readDoubleInRange("Final exam: ", 0, 40);

        try {
            Mark mark = new Mark(first, second, finalExam);
            teacher.putMark(student, course, mark);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper method for creating a lesson by teacher with course selection, lesson
    // details input, and lesson creation
    private static void createLesson(Database database, Teacher teacher) {
        Course course = selectCourse(database);

        if (course == null) {
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

    // Helper method for marking attendance for a lesson by teacher with course
    // selection, lesson selection, student selection, and attendance marking
    private static void markAttendance(Database database, Teacher teacher) {
        Course course = selectCourse(database);

        if (course == null) {
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

        Student student = selectStudent(database);

        if (student == null) {
            return;
        }

        System.out.print("Present? yes/no: ");
        String answer = readLine();

        boolean present = answer.equalsIgnoreCase("yes")
                || answer.equalsIgnoreCase("y")
                || answer.equalsIgnoreCase("true");

        teacher.markAttendance(lesson, student, present);

        System.out.println();
        lesson.printAttendance();
    }

    // Helper method for rating a teacher by student with teacher selection, course
    // selection, and rating input
    private static void rateTeacher(Database database, Student student) {
        Teacher teacher = selectTeacher(database);

        if (teacher == null) {
            return;
        }

        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        int rating = readIntInRange("Rating 1-5: ", 1, 5);

        System.out.print("Comment: ");
        String comment = readLine();

        try {
            student.rateTeacher(teacher, course, rating, comment);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper method for viewing course details by student with course selection and
    // course details display
    private static void viewCourseDetails(Database database) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        course.printCourseDetails();

        Database.getInstance().addLog(
                "SYSTEM: course details viewed for " + course.getCode()
        );
    }

    // Helper method for removing a user by admin with username input, user
    // selection, confirmation, and user removal
    private static void removeUser(Database database, Admin admin) {
        System.out.print("Username to remove: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if (user == admin) {
            System.out.println("Admin cannot remove himself/herself.");
            return;
        }

        System.out.println("Are you sure you want to remove " + user.getFullName() + "? yes/no");
        String answer = readLine();

        if (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("y")) {
            System.out.println("Remove cancelled.");
            return;
        }

        admin.removeUser(database, user);
        System.out.println("User removed successfully.");
    }

    private static void adminAddUser(Database database, Admin admin) {
        System.out.println();
        System.out.println("=== Admin Add User ===");

        User user = createUserFromInput(database);

        if (user == null) {
            return;
        }

        admin.addUser(database, user);
        System.out.println("User was added successfully by admin.");
    }

    // Helper method for resolving a complaint by admin with received complaints
    // display, selection, and complaint resolution
    private static void resolveComplaint(Admin admin) {
        List<Complaint> complaints = admin.getReceivedComplaints();

        if (complaints == null || complaints.isEmpty()) {
            System.out.println("No received complaints found.");
            return;
        }

        System.out.println("Received complaints:");
        System.out.println("--------------------------------");

        for (int i = 0; i < complaints.size(); i++) {
            Complaint complaint = complaints.get(i);

            System.out.println((i + 1) + ". " + complaint.getSubject() +
                    " | From: " + complaint.getSender().getFullName() +
                    " | Status: " + (complaint.isResolved() ? "RESOLVED" : "OPEN"));
        }

        System.out.print("Choose complaint number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= complaints.size()) {
            System.out.println("Wrong complaint number.");
            return;
        }

        admin.resolveComplaint(complaints.get(index));
    }

    private static void printTopCitedResearcherBySchool(Database database) {
        System.out.print("School/department: ");
        String school = readLine();

        database.printTopCitedResearcherBySchool(school);
    }

    private static void printTopCitedResearcherBySchoolAndYear(Database database) {
        System.out.print("School/department: ");
        String school = readLine();

        System.out.print("Year: ");
        int year = readInt();

        database.printTopCitedResearcherBySchoolAndYear(school, year);
    }

    // Helper method for adding a new research paper by researcher with paper
    // details input and paper creation
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
                doi);

        researcher.addResearchPaper(paper);
        database.addResearchPaper(paper);

        System.out.println("Research paper added successfully.");
    }

    // Helper method for printing research papers of a researcher by researcher with
    // papers display sorted by the given comparator
    private static void printResearchPapers(Researcher researcher, java.util.Comparator<ResearchPaper> comparator) {
        if (researcher.getResearchPapers() == null || researcher.getResearchPapers().isEmpty()) {
            System.out.println("No research papers found.");
            return;
        }

        researcher.printPapers(comparator);
    }

    // Helper method for adding a research paper to a research project by researcher
    // with project selection, paper selection, and adding the paper to the project
    private static void addPaperToResearchProject(Database database, User user, Researcher researcher) {
        ResearchProject project = selectResearchProject(database);

        if (project == null) {
            return;
        }

        List<ResearchPaper> papers = researcher.getResearchPapers();

        if (papers == null || papers.isEmpty()) {
            System.out.println("You have no research papers.");
            return;
        }

        System.out.println("Your papers:");

        for (int i = 0; i < papers.size(); i++) {
            System.out.println((i + 1) + ". " + papers.get(i).getTitle());
        }

        System.out.print("Choose paper number: ");
        int paperIndex = readInt() - 1;

        if (paperIndex < 0 || paperIndex >= papers.size()) {
            System.out.println("Wrong paper number.");
            return;
        }

        ResearchPaper paper = papers.get(paperIndex);

        project.addPaper(researcher, paper);
    }

    // Helper method for creating a research project by researcher with project
    // topic input, project creation, and joining the project
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

    // Helper method for joining a research project by researcher with project
    // selection and joining the project
    private static void joinResearchProject(Database database, User user, Researcher researcher) {
        ResearchProject project = selectResearchProject(database);

        if (project == null) {
            return;
        }

        try {
            researcher.joinProject(project);
            System.out.println("Joined research project successfully.");
        } catch (NotResearcherException e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper method for viewing research projects of a researcher with project
    // display
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

    // Helper method for viewing papers of a research project by researcher with
    // project selection and papers display
    private static void viewProjectPapers(Database database) {
        ResearchProject project = selectResearchProject(database);

        if (project == null) {
            return;
        }

        project.printPublishedPapers();
    }

    // Helper method for creating news by manager with news details input and news
    // creation
    private static void createNews(Manager manager) {
        System.out.print("News title: ");
        String title = readLine();

        System.out.print("News content: ");
        String content = readLine();

        manager.createNews(title, content);
    }

    // Helper method for generating a course report by manager with course selection
    // and report generation
    private static void generateCourseReport(Database database, Manager manager) {
        Course course = selectCourse(database);

        if (course == null) {
            return;
        }

        manager.generateReport(new CoursePerformanceReport(course));
    }

    // Helper method for adding a prerequisite to a course by manager with course
    // and prerequisite selection
    private static void addPrerequisiteToCourse(Database database) {
        printCourses(database);

        System.out.print("Course code: ");
        String courseCode = readLine();

        Course course = database.findCourseByCode(courseCode);

        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        System.out.print("Prerequisite course code: ");
        String prerequisiteCode = readLine();

        Course prerequisite = database.findCourseByCode(prerequisiteCode);

        if (prerequisite == null) {
            System.out.println("Prerequisite course not found.");
            return;
        }

        course.addPrerequisite(prerequisite);

        Database.getInstance().addLog(
                "MANAGER: added prerequisite " +
                        prerequisite.getCode() +
                        " to course " + course.getCode());

        System.out.println("Prerequisite added successfully.");
    }

    // Helper method for showing top cited researcher by year with year input and
    // top cited researcher display
    private static void printTopCitedResearcherByYear(Database database) {
        System.out.print("Year: ");
        int year = readInt();

        database.printTopCitedResearcherByYear(year);
    }

    // Helper method for sending a message to another employee by employee with
    // recipient selection, message text input, and message sending
    private static void sendEmployeeMessage(Database database, Employee sender) {
        Employee recipient = selectEmployee(database);

        if (recipient == null) {
            return;
        }

        System.out.print("Message text: ");
        String text = readLine();

        sender.sendMessage(recipient, text);
        }

    // Helper method for sending a complaint to another employee by employee with
    // recipient selection, complaint details input, and complaint sending
    private static void sendEmployeeComplaint(Database database, Employee sender) {
        Employee recipient = selectEmployee(database);

        if (recipient == null) {
            return;
        }

        System.out.print("Complaint subject: ");
        String subject = readLine();

        System.out.print("Complaint description: ");
        String description = readLine();

        sender.sendComplaint(recipient, subject, description);
    }

    // Helper method for creating an employee request by employee with request
    // details input and request creation
    private static void createEmployeeRequest(Employee employee) {
        System.out.print("Request title: ");
        String title = readLine();

        System.out.print("Request description: ");
        String description = readLine();

        employee.createRequest(title, description);
    }

    // Helper method for processing employee requests by manager with signed
    // requests display, selection, and approval/rejection
    private static void processEmployeeRequest(Database database, Manager manager, boolean approve) {
        List<EmployeeRequest> signedRequests = database.getSignedEmployeeRequests();

        if (signedRequests.isEmpty()) {
            System.out.println("No signed employee requests found.");
            return;
        }

        for (int i = 0; i < signedRequests.size(); i++) {
            EmployeeRequest request = signedRequests.get(i);

            System.out.println((i + 1) + ". " + request.getTitle() +
                    " from " + request.getSender().getFullName() +
                    " | Signed by: " + request.getSignedBy().getFullName());
        }

        System.out.print("Choose request number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= signedRequests.size()) {
            System.out.println("Wrong request number.");
            return;
        }

        EmployeeRequest request = signedRequests.get(index);

        if (approve) {
            manager.approveRequest(request);
        } else {
            manager.rejectRequest(request);
        }
    }

    // Helper method for signing an employee request by employee with requests
    // display, selection, and signing
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

    // Helper method for setting the signing role of an employee by admin with
    // employee selection, role selection, and role assignment
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

    // Helper method for updating the username of a user by admin with user
    // selection, new username input, and username update
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

    // Helper method for updating the password of a user by admin with user
    // selection, new password input, and password update
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

    // Helper method for updating the full name of a user by admin with user
    // selection, new full name input, and full name update
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

    // Helper method for printing available courses with course display
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

    // ===== Selection helpers =====

    private static Course selectCourse(Database database) {
        printCourses(database);

        System.out.print("Course code: ");
        String code = readLine();

        Course course = database.findCourseByCode(code);

        if (course == null) {
            System.out.println("Course not found.");
            return null;
        }

        return course;
    }

    private static User selectUser(Database database) {
        System.out.print("Username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (user == null) {
            System.out.println("User not found.");
            return null;
        }

        return user;
    }

    private static Student selectStudent(Database database) {
        System.out.print("Student username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Student)) {
            System.out.println("Student not found.");
            return null;
        }

        return (Student) user;
    }

    private static Teacher selectTeacher(Database database) {
        System.out.print("Teacher username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Teacher)) {
            System.out.println("Teacher not found.");
            return null;
        }

        return (Teacher) user;
    }

    private static Employee selectEmployee(Database database) {
        System.out.print("Employee username: ");
        String username = readLine();

        User user = database.findUserByUsername(username);

        if (!(user instanceof Employee)) {
            System.out.println("Employee not found.");
            return null;
        }

        return (Employee) user;
    }

    private static ResearchProject selectResearchProject(Database database) {
        List<ResearchProject> projects = database.getResearchProjects();

        if (projects == null || projects.isEmpty()) {
            System.out.println("No research projects found.");
            return null;
        }

        System.out.println("Research projects:");

        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getTopic());
        }

        System.out.print("Choose project number: ");
        int index = readInt() - 1;

        if (index < 0 || index >= projects.size()) {
            System.out.println("Wrong project number.");
            return null;
        }

        return projects.get(index);
    }

    // Helper method for viewing user details by username with user selection and
    // user details display
    private static void viewUserDetails(Database database) {
        User user = selectUser(database);

        if (user == null) {
            return;
        }

        if (user instanceof Student) {
            ((Student) user).printStudentDetails();
        } else if (user instanceof Teacher) {
            ((Teacher) user).printTeacherDetails();
        } else if (user instanceof Employee) {
            ((Employee) user).printEmployeeDetails();
        } else {
            System.out.println("=== User Details ===");
            System.out.println("Full name: " + user.getFullName());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Role: " + user.getRoleName());
        }

        Database.getInstance().addLog(
                "SYSTEM: user details viewed for " + user.getUsername()
        );
    }

    // Helper method for choosing a database file on application start with database
    // files display, selection, and database loading/creation
    private static Database chooseDatabaseOnStart() {
        while (true) {
            java.util.List<Path> files = getDatabaseFiles();

            System.out.println();
            System.out.println("=== Database Selection ===");

            if (files.isEmpty()) {
                System.out.println("No saved databases found.");
            } else {
                System.out.println("Saved databases:");

                for (int i = 0; i < files.size(); i++) {
                    System.out.println((i + 1) + ". " + files.get(i).getFileName());
                }
            }

            System.out.println((files.size() + 1) + ". Create new database");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = readInt();

            if (choice == 0) {
                System.out.println("Goodbye!");
                System.exit(0);
            }

            if (choice >= 1 && choice <= files.size()) {
                Path selectedFile = files.get(choice - 1);

                Database database = Database.tryLoad(selectedFile.toString());

                if (database != null) {
                    currentDataFile = selectedFile.toString();
                    return database;
                }

                System.out.println();
                System.out.println("This database could not be loaded.");
                System.out.println("It was NOT overwritten.");
                System.out.println("Choose another database or create a new one.");
                continue;
            }

            if (choice == files.size() + 1) {
                currentDataFile = createNewDatabaseFileName();

                Database database = Database.createNew();

                System.out.println("New database was created.");
                System.out.println("It will be saved to: " + currentDataFile);

                return database;
            }

            System.out.println("Wrong option.");
        }
    }

    // Helper method for getting a list of database files from the database
    // directory with .ser extension and sorting by name in descending order
    private static java.util.List<Path> getDatabaseFiles() {
        java.util.List<Path> files = new ArrayList<>();

        try {
            Path dir = Paths.get(DB_DIR);
            Files.createDirectories(dir);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.ser")) {
                for (Path path : stream) {
                    files.add(path);
                }
            }

            files.sort((p1, p2) -> p2.getFileName().toString().compareToIgnoreCase(p1.getFileName().toString()));

        } catch (IOException e) {
            System.out.println("Could not read database directory: " + e.getMessage());
        }

        return files;
    }

    // Helper method for creating a new database file name with timestamp and
    // ensuring the database directory exists
    private static String createNewDatabaseFileName() {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
        } catch (IOException e) {
            System.out.println("Could not create database directory: " + e.getMessage());
        }

        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return DB_DIR + "/university_" + timestamp + ".ser";
    }

    // Helper method for reading a line of input from the user with trimming
    private static String readLine() {
        return scanner.nextLine().trim();
    }

    // Helper method for reading an integer input from the user with validation and
    // error handling
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

    // Helper method for reading a double input from the user with validation and
    // error handling
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

    // Helper method for reading a date input from the user with validation and
    // error handling
    private static LocalDate readDate() {
        while (true) {
            try {
                System.out.print("Date yyyy-MM-dd: ");
                String input = readLine();
                LocalDate date = LocalDate.parse(input);

                int year = date.getYear();

                if (year < 1900 || year > LocalDate.now().getYear()) {
                    System.out.println("Year must be between 1900 and current year.");
                    continue;
                }

                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Wrong date format. Example: 2026-05-01");
            }
        }
    }

    // Helper method for reading a date and time input from the user with validation
    // and error handling
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

    // Helper method for reading an enum value from the user with display of
    // options, validation, and error handling
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

    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            int value = readInt();

            if (value >= min && value <= max) {
                return value;
            }

            System.out.println("Value must be between " + min + " and " + max + ".");
        }
    }

    private static double readDoubleInRange(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            double value = readDouble();

            if (value >= min && value <= max) {
                return value;
            }

            System.out.println("Value must be between " + min + " and " + max + ".");
        }
    }

    private static String readRequiredLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = readLine();

            if (value != null && !value.isBlank()) {
                return value;
            }

            System.out.println("Value cannot be empty.");
        }
    }

    private static String readMajorOrAll(String prompt) {
        while (true) {
            String major = readRequiredLine(prompt);

            if (major.equalsIgnoreCase("ALL")) {
                return "ALL";
            }

            if (major.matches("[A-Za-zА-Яа-я ]{2,}")) {
                return major;
            }

            System.out.println("Major must contain letters only, or enter ALL.");
        }
    }
}