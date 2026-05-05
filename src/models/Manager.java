package models;

import enums.ManagerType;
import enums.RegistrationStatus;
import enums.RequestStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Manager extends Employee {
    private ManagerType managerType;
    private List<Registration> registrations = new ArrayList<>();

    public Manager(String id, String username, String password, String fullName,
                   double salary, String department, ManagerType managerType) {
        super(id, username, password, fullName, salary, department);
        this.managerType = managerType;
    }

    public void addRegistration(Registration registration) {
        registrations.add(registration);
    }

    // A manager can only approve a registration if:
    // 1. The student will not exceed the credit limit of 21 credits after adding the course.
    // 2. The course is available for the student's major and year.
    // 3. The student has not failed 3 or more courses.
    public void approveRegistration(Registration registration) {
        if (registration == null) {
            System.out.println("Registration does not exist.");
            return;
        }

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            System.out.println("Registration is already processed.");
            return;
        }

        Student student = registration.getStudent();
        Course course = registration.getCourse();

        if (student.getTotalCredits() + course.getCredits() > 21) {
            System.out.println("Cannot approve: credit limit exceeded.");
            registration.reject();

            Database.getInstance().addLog(
                "MANAGER: " + getFullName() +
                " rejected registration of " + student.getFullName() +
                " for course " + course.getName() +
                " because credit limit was exceeded"
            );

            return;
        }

        if (!course.isAvailableFor(student)) {
            System.out.println("Cannot approve: course is not intended for student's major/year.");
            registration.reject();

            Database.getInstance().addLog(
                    "MANAGER: " + getFullName() +
                            " rejected registration of " + student.getFullName() +
                            " for course " + course.getName() +
                            " because course is not intended for student's major/year."
            );

            return;
        }

        if (student.getFailedCoursesCount() >= 3) {
            System.out.println("Cannot approve: student has failed 3 or more courses.");
            registration.reject();

            Database.getInstance().addLog(
                    "MANAGER: " + getFullName() +
                            " rejected registration of " + student.getFullName() +
                            " for course " + course.getName() +
                            " because student has failed 3 or more courses."
            );

            return;
        }

        registration.approve();
        student.addCourse(course);
        course.addStudent(student);

        System.out.println("Registration approved: " + registration);

        Database.getInstance().addLog(
                "MANAGER: " + getFullName() +
                " approved registration of " + student.getFullName() +
                " for course " + course.getName()
        );
    }

    // A manager can reject a registration if it is still pending.
    public void rejectRegistration(Registration registration) {
        if (registration == null) {
            System.out.println("Registration does not exist.");
            return;
        }
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            System.out.println("Registration is already processed.");
            return;
        }

        registration.reject();
        System.out.println("Registration rejected: " + registration);

        Database.getInstance().addLog(
                "MANAGER: " + getFullName() +
                " rejected registration of " + registration.getStudent().getFullName() +
                " for course " + registration.getCourse().getName()
        );

    }

    // A manager can assign a teacher to a course.
    public void assignTeacher(Course course, Teacher teacher) {
        teacher.assignCourse(course);

        Database.getInstance().addLog(
            "MANAGER: " + getFullName() +
            " assigned teacher " + teacher.getFullName() +
            " to course " + course.getName()
        );
    }

    // A manager can generate various reports using different strategies.
    public void generateReport(ReportStrategy strategy) {
        if (strategy == null) {
            System.out.println("Report strategy does not exist.");
            return;
        }

        strategy.generate(Database.getInstance());

        Database.getInstance().addLog(
                "MANAGER: " + getFullName() + " generated report: " + strategy.getName()
        );
    }

    // A manager can create news to be published.
    public void createNews(String title, String content) {
        News news = new News(title, content, this);

        Database.getInstance().publishNews(news);

        System.out.println("News was published successfully.");
    }

    // A manager can view all employee requests that have been signed but not yet approved or rejected.
    public void viewSignedRequests() {
        Database.getInstance().printSignedEmployeeRequests();

        Database.getInstance().addLog(
                "MANAGER: " + getFullName() + " viewed signed employee requests."
        );
    }

    // A manager can approve employee requests that are signed and pending approval.
    public void approveRequest(EmployeeRequest request) {
        if (request == null) {
            System.out.println("Request does not exist.");
            return;
        }

        if (request.getStatus() != RequestStatus.SIGNED) {
            System.out.println("Only signed requests can be approved.");
            return;
        }

        request.approve();

        Database.getInstance().addLog(
                "MANAGER: " + getFullName() +
                        " approved request '" + request.getTitle() +
                        "' from " + request.getSender().getFullName()
        );

        System.out.println("Request approved successfully.");
    }

    // A manager can reject employee requests that are signed and pending approval.
    public void rejectRequest(EmployeeRequest request) {
        if (request == null) {
            System.out.println("Request does not exist.");
            return;
        }

        if (request.getStatus() != RequestStatus.SIGNED) {
            System.out.println("Only signed requests can be rejected.");
            return;
        }

        request.reject();

        Database.getInstance().addLog(
                "MANAGER: " + getFullName() +
                        " rejected request '" + request.getTitle() +
                        "' from " + request.getSender().getFullName()
        );

        System.out.println("Request rejected successfully.");
    }

    // A manager can view all students sorted by GPA in descending order.
    public void viewStudentsSortedByGpa(Database database) {
        List<Student> students = new ArrayList<>();

        for (User user : database.getUsers()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        students.sort((s1, s2) -> Double.compare(s2.calculateGpa(), s1.calculateGpa()));

        System.out.println("Students sorted by GPA:");
        System.out.println("--------------------------------");

        for (Student student : students) {
            System.out.println("Name: " + student.getFullName());
            System.out.println("Username: " + student.getUsername());
            System.out.println("Major: " + student.getMajor());
            System.out.println("Year: " + student.getYear());
            System.out.printf("GPA: %.2f%n", student.calculateGpa());
            System.out.println("--------------------------------");
        }

        Database.getInstance().addLog(
            "MANAGER: " + getFullName() + " viewed students sorted by GPA."
        );
    }

    public void viewStudentsAlphabetically(Database database) {
        List<Student> students = new ArrayList<>();

        for (User user : database.getUsers()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        students.sort((s1, s2) -> s1.getFullName().compareToIgnoreCase(s2.getFullName()));

        System.out.println("Students alphabetically:");
        System.out.println("--------------------------------");

        for (Student student : students) {
            System.out.println("Name: " + student.getFullName());
            System.out.println("Username: " + student.getUsername());
            System.out.println("Major: " + student.getMajor());
            System.out.println("Year: " + student.getYear());
            System.out.printf("GPA: %.2f%n", student.calculateGpa());
            System.out.println("--------------------------------");
        }

        Database.getInstance().addLog(
            "MANAGER: " + getFullName() + " viewed students alphabetically."
        );
    }

    public void viewTeachersAlphabetically(Database database) {
        List<Teacher> teachers = new ArrayList<>();

        for (User user : database.getUsers()) {
            if (user instanceof Teacher) {
                teachers.add((Teacher) user);
            }
        }

        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }

        teachers.sort((t1, t2) -> t1.getFullName().compareToIgnoreCase(t2.getFullName()));

        System.out.println("Teachers alphabetically:");
        System.out.println("--------------------------------");

        for (Teacher teacher : teachers) {
            System.out.println("Name: " + teacher.getFullName());
            System.out.println("Username: " + teacher.getUsername());
            System.out.println("Department: " + teacher.getDepartment());
            System.out.println("Title: " + teacher.getTitle());
            System.out.println("H-index: " + teacher.getHIndex());
            System.out.printf("Average rating: %.2f/5%n", teacher.getAverageRating());
            System.out.println("--------------------------------");
        }

        Database.getInstance().addLog(
            "MANAGER: " + getFullName() + " viewed teachers alphabetically."
        );
    }

    public ManagerType getManagerType() {
        return managerType;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    @Override
    public void showMenu() {
        System.out.println("Manager menu");
    }
}