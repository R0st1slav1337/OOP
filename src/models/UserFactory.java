package models;

import enums.ManagerType;
import enums.TeacherTitle;
import enums.UserType;

public class UserFactory {
    public static User createUser(UserType userType, String id, String username, 
                    String password, String fullName) {

        User user;

        switch (userType) {
            case STUDENT:
                user = new Student(
                        id,
                        username,
                        password,
                        fullName,
                        1,
                        "Undeclared",
                        0.0,
                        0
                );
                break;
            
            case TEACHER:
                user = new Teacher(
                        id,
                        username,
                        password,
                        fullName,
                        0,
                        "Unknown department",
                        TeacherTitle.TUTOR,
                        0
                );
                break;

            case MANAGER:
                user = new Manager(
                        id,
                        username,
                        password,
                        fullName,
                        0,
                        "Unknown department",
                        ManagerType.OR
                );
                break;
                
            case ADMIN:
                user = new Admin(
                        id,
                        username,
                        password,
                        fullName,
                        0,
                        "Administration"
                );
                break;
        
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }

        Database.getInstance().addLog(
            "FACTORY: created " + userType + " user " + fullName
        );

        return user;
    }
}
