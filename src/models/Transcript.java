package models;

import java.util.HashMap;
import java.util.Map;

public class Transcript {
    private Student student;
    private Map<Course, Mark> marks = new HashMap<>();

    public Transcript(Student student) {
        this.student = student;
    }

    public void addMark(Course course, Mark mark) {
        marks.put(course, mark);
    }

    public double calculateGpa() {
        if (marks.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0;
        int totalCredits = 0;

        for (Course course : marks.keySet()) {
            Mark mark = marks.get(course);

            totalPoints += mark.getGpaPoints() * course.getCredits();
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        return totalPoints / totalCredits;
    }

    public void printTranscript() {
        System.out.println("Transcript of " + student.getFullName());
        System.out.println("--------------------------------");

        for (Course course : marks.keySet()) {
            Mark mark = marks.get(course);

            System.out.println(course.getCode() + " - " + course.getName());
            System.out.println("Credits: " + course.getCredits());
            System.out.println("Mark: " + mark);
            System.out.println("--------------------------------");
        }

        System.out.printf("GPA: %.2f%n", calculateGpa());
    }
    
}