package models;

import enums.ManagerType;
import enums.RegistrationStatus;

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

    public void assignTeacher(Course course, Teacher teacher) {
        teacher.assignCourse(course);

        Database.getInstance().addLog(
            "MANAGER: " + getFullName() +
            " assigned teacher " + teacher.getFullName() +
            " to course " + course.getName()
        );
    }

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

    public void createNews(String title, String content) {
        News news = new News(title, content, this);

        Database.getInstance().publishNews(news);

        System.out.println("News was published successfully.");
    }

    public void viewStudentsSortedByGpa(List<Student> students) {
        students.stream().sorted(Comparator.comparing(Student::getGpa).reversed())
        .forEach(System.out::println);
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