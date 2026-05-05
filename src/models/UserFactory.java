package models;

import enums.ManagerType;
import enums.TeacherTitle;
import enums.UserType;

public class UserFactory {
    public static User createUser(
            UserType userType,
            String id,
            String username,
            String password,
            String fullName
    ) {
        User user;

        switch (userType) {
            case STUDENT:
                user = createStudent(id, username, password, fullName, 1, "Undeclared", 0);
                break;

            case TEACHER:
                user = createTeacher(id, username, password, fullName, 0, "Unknown department", TeacherTitle.TUTOR, 0);
                break;

            case MANAGER:
                user = createManager(id, username, password, fullName, 0, "Unknown department", ManagerType.OR);
                break;

            case ADMIN:
                user = createAdmin(id, username, password, fullName, 0, "Administration");
                break;

            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }

        return user;
    }

    public static Student createStudent(
            String id,
            String username,
            String password,
            String fullName,
            int year,
            String major,
            int hIndex
    ) {
        Student student = new Student(
                id,
                username,
                password,
                fullName,
                year,
                major,
                0.0,
                hIndex
        );

        Database.getInstance().addLog(
                "FACTORY: created STUDENT user " + fullName
        );

        return student;
    }

    public static Teacher createTeacher(
            String id,
            String username,
            String password,
            String fullName,
            double salary,
            String department,
            TeacherTitle title,
            int hIndex
    ) {
        Teacher teacher = new Teacher(
                id,
                username,
                password,
                fullName,
                salary,
                department,
                title,
                hIndex
        );

        Database.getInstance().addLog(
                "FACTORY: created TEACHER user " + fullName
        );

        return teacher;
    }

    public static Manager createManager(
            String id,
            String username,
            String password,
            String fullName,
            double salary,
            String department,
            ManagerType managerType
    ) {
        Manager manager = new Manager(
                id,
                username,
                password,
                fullName,
                salary,
                department,
                managerType
        );

        Database.getInstance().addLog(
                "FACTORY: created MANAGER user " + fullName
        );

        return manager;
    }

    public static Admin createAdmin(
            String id,
            String username,
            String password,
            String fullName,
            double salary,
            String department
    ) {
        Admin admin = new Admin(
                id,
                username,
                password,
                fullName,
                salary,
                department
        );

        Database.getInstance().addLog(
                "FACTORY: created ADMIN user " + fullName
        );

        return admin;
    }
}