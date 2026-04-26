package models;

import enums.ManagerType;

public class Manager extends Employee {
    private ManagerType managerType;

    public Manager(String id, String username, String password, String fullName,
                   double salary, ManagerType managerType) {
        super(id, username, password, fullName, salary);
        this.managerType = managerType;
    }

    public void approveRegistration(Student student, Course course) {
        course.addStudent(student);
        System.out.println("Registration approved.");
    }

    public void assignTeacher(Course course, Teacher teacher) {
        course.addInstructor(teacher);
    }

    @Override
    public void showMenu() {
        System.out.println("Manager menu");
    }
}