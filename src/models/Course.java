package models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String code;
    private String name;
    private int credits;
    private List<Teacher> instructors = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Lesson> lessons = new ArrayList<>();

    public Course(String code, String name, int credits) {
        this.code = code;
        this.name = name;
        this.credits = credits;
    }

    public void addInstructor(Teacher teacher) {
        instructors.add(teacher);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
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

    @Override
    public String toString() {
        return code + " - " + name + " (" + credits + " credits)";
    }
    
}