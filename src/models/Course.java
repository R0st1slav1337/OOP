package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private int credits;
    private String intendedMajor;
    private int intendedYear;

    private List<Teacher> instructors = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Lesson> lessons = new ArrayList<>();

    // default constructor
    public Course(String code, String name, int credits) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.intendedMajor = "ALL";
        this.intendedYear = 0;
    }

    public Course(String code, String name, int credits, String intendedMajor, int intendedYear) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.intendedMajor = intendedMajor;
        this.intendedYear = intendedYear;
    }

    public void addInstructor(Teacher teacher) {
        if (!instructors.contains(teacher)) {
            instructors.add(teacher);
        }
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }

        if (!students.contains(student)) {
            students.add(student);

            if (lessons != null) {
                for (Lesson lesson : lessons) {
                    lesson.addStudent(student);
                }
            }
        }
    }

    public boolean isAvailableFor(Student student) {
        String major = intendedMajor;

        if (major == null || major.isBlank()) {
            major = "ALL";
        }

        boolean majorMatches = intendedMajor.equalsIgnoreCase("ALL")
                || intendedMajor.equalsIgnoreCase(student.getMajor());

        boolean yearMatches = intendedYear == 0
                || intendedYear == student.getYear();

        return majorMatches && yearMatches;
    }

    public void addLesson(Lesson lesson) {
        if (lessons == null) {
            lessons = new ArrayList<>();
        }

        if (lesson == null) {
            System.out.println("Lesson does not exist.");
            return;
        }

        if (!lessons.contains(lesson)) {
            lessons.add(lesson);
        }
    }

    public void printSchedule() {
        if (lessons == null || lessons.isEmpty()) {
            System.out.println("No lessons for course " + name);
            return;
        }

        System.out.println("Schedule for course: " + name);
        System.out.println("--------------------------------");

        for (Lesson lesson : lessons) {
            System.out.println(lesson);
        }
    }

    public int getCredits() {
        return credits;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<Teacher> getInstructors() {
        return instructors;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public String getIntendedMajor() {
        return intendedMajor;
    }

    public int getIntendedYear() {
        return intendedYear;
    }

    @Override
    public String toString() {
        return code + " - " + name +
            " (" + credits + " credits)" +
            " | major: " + intendedMajor +
            " | year: " + (intendedYear == 0 ? "ALL" : intendedYear);
    }
    
}