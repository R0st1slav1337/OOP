package models;

import enums.ManagerType;

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
        registration.approve();
    }

    public void rejectRegistration(Registration registration) {
        registration.reject();
    }

    public void assignTeacher(Course course, Teacher teacher) {
        // teacher.assignCourse(course);
        course.addInstructor(teacher);
    }

    public void viewStudentsSortedByGpa(List<Student> students) {
        students.stream().sorted(Comparator.comparing(Student::getGpa).reversed())
        .forEach(System.out::println);
    }

    public ManagerType getManagerType() {
        return managerType;
    }

    @Override
    public void showMenu() {
        System.out.println("Manager menu");
    }
}