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
            return;
        }

        registration.approve();
        student.addCourse(course);
        course.addStudent(student);

        System.out.println("Registration approved: " + registration);
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
    }

    public void assignTeacher(Course course, Teacher teacher) {
        teacher.assignCourse(course);
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