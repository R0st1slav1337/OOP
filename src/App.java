import enums.ManagerType;
import enums.TeacherTitle;
import enums.UserType;
import models.*;

import java.time.LocalDate;
import java.util.List;
import exceptions.NotResearcherException;

public class App {
    public static void main(String[] args) {
        Database database = Database.getInstance();

        Student student = new Student(
                "S1",
                "student1",
                "123",
                "John Student",
                2,
                "Computer Science",
                3.5,
                0
        );

        User factoryStudent = UserFactory.createUser(
            UserType.STUDENT,
            "S2",
            "student2",
            "123",
            "Factory Student"
        );

        database.addUser(factoryStudent);
        System.out.println("Factory test:");
        System.out.println(factoryStudent);

        Manager manager = new Manager(
                "M1",
                "manager1",
                "123",
                "Main Manager",
                500000,
                "OR",
                ManagerType.OR
        );

        Teacher teacher = new Teacher(
                "T1",
                "teacher1",
                "123",
                "Alan Teacher",
                700000,
                "CS",
                TeacherTitle.PROFESSOR,
                10
        );

        Admin admin = new Admin(
                "A1",
                "admin1",
                "123",
                "Main Admin",
                800000,
                "IT"
        );

        database.addUser(admin);
        database.addUser(student);
        database.addUser(teacher);
        database.addUser(manager);

        Course oop = new Course("CS101", "Object-Oriented Programming", 5);
        database.addCourse(oop);

        manager.assignTeacher(oop, teacher);

        Registration registration = student.requestRegistration(oop);
        manager.addRegistration(registration);
        manager.approveRegistration(registration);

        System.out.println("Student courses:");
        student.viewCourses();

        System.out.println("Course students:");
        for (Student s : oop.getStudents()) {
            System.out.println(s.getFullName());
        }

        System.out.println("Student total credits: " + student.getTotalCredits());

        Mark mark = new Mark(27, 28, 35);

        teacher.putMark(student, oop, mark);

        System.out.println();
        student.viewMarks();

        System.out.println();
        Transcript transcript = student.getTranscript();
        transcript.printTranscript();

        System.out.println();
        System.out.println("=== Research test ===");
        
        ResearchPaper paper1 = new ResearchPaper(
                "Object-Oriented Design in University Systems",
                List.of(teacher),
                "Journal of Software Engineering",
                12,
                25,
                LocalDate.of(2023, 5, 10),
                "10.1000/oop.2023.001"
        );
        
        ResearchPaper paper2 = new ResearchPaper(
                "Research-Based Learning Models",
                List.of(student, teacher),
                "Education and Research Journal",
                8,
                10,
                LocalDate.of(2024, 3, 15),
                "10.1000/edu.2024.002"
        );
        
        teacher.addResearchPaper(paper1);
        student.addResearchPaper(paper2);
        
        ResearchProject project = new ResearchProject("AI-based University Information System");
        
        try {
            teacher.joinProject(project);
            student.joinProject(project);
        } catch (NotResearcherException e) {
            System.out.println(e.getMessage());
        }
        
        project.addPaper(paper1);
        project.addPaper(paper2);
        
        database.addResearchPaper(paper1);
        database.addResearchPaper(paper2);
        database.addResearchProject(project);
        
        System.out.println();
        System.out.println("Teacher papers sorted by citations:");
        teacher.printPapers(ResearchPaperComparators.BY_CITATIONS);
        
        System.out.println();
        project.printParticipants();
        
        System.out.println();
        database.printAllResearchPapers(ResearchPaperComparators.BY_DATE);
        
        System.out.println();
        database.printTopCitedResearcher();

        System.out.println();
        System.out.println("=== Authentication test ===");

        User loggedInUser = database.authenticate("student1", "123");

        if (loggedInUser != null) {
            System.out.println("Logged in as: " + loggedInUser.getFullName());
            loggedInUser.showMenu();
        } else {
            System.out.println("Wrong username or password.");
        }

        User failedLogin = database.authenticate("student1", "wrongpassword");

        if (failedLogin == null) {
           System.out.println("Login failed as expected.");
        }

        System.out.println();
        System.out.println("=== Logs test ===");
        admin.viewLogs(database);



        database.save("university.ser");
    }
}