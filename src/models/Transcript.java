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

    public void printTranscript() {
        System.out.println("Transcript of " + student.getFullName());

        for (Course course : marks.keySet()) {
            System.out.println(course + " -> " + marks.get(course));
        }
    }
    
}