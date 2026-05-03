package models;

import enums.LessonType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topic;
    private LessonType type;
    private Course course;
    private Teacher instructor;
    private LocalDateTime dateTime;
    private String room;

    private List<Student> students = new ArrayList<>();
    private Map<Student, Boolean> attendance = new HashMap<>();

    public Lesson(String topic, LessonType type, Course course) {
        this(topic, type, course, null, null, "Not assigned");
    }

    public Lesson(String topic, LessonType type, Course course, Teacher instructor, 
                    LocalDateTime dateTime, String room) {
        this.topic = topic;
        this.type = type;
        this.course = course;
        this.instructor = instructor;
        this.dateTime = dateTime;
        this.room = room;

        if (course != null) {
            for (Student student : course.getStudents()) {
                addStudent(student);
            }
        }
    }

    public void addStudent(Student student) {
        if (student == null) {
            return;
        }

        if (course != null && !course.getStudents().contains(student)) {
            System.out.println("Cannot add student to lesson: student is not registered for this course.");
            return;
        }

        if (!students.contains(student)) {
            students.add(student);
            attendance.put(student, false);
        }
    }

    public void markAttendance(Student student, boolean present) {
        if (student == null) {
            System.out.println("Student does not exist.");
            return;
        }

        if (!students.contains(student)) {
            System.out.println("Cannot mark attendance: student is not in this lesson.");
            return;
        }

        attendance.put(student, present);
    }

    public void printAttendance() {
        System.out.println("Attendance for lesson: " + topic);
        System.out.println("--------------------------------");

        if (students.isEmpty()) {
            System.out.println("No students in this lesson.");
            return;
        }

        for (Student student : students) {
            boolean present = attendance.getOrDefault(student, false);

            System.out.println(student.getFullName() + " - " + (present ? "Present" : "Absent"));
        }
    }

    public void printLessonInfo() {
        System.out.println("Lesson info:");
        System.out.println("Topic: " + topic);
        System.out.println("Type: " + type);
        System.out.println("Course: " + (course == null ? "Not assigned" : course.getName()));
        System.out.println("Instructor: " + (instructor == null ? "Not assigned" : instructor.getFullName()));
        System.out.println("Date/time: " + (dateTime == null ? "Not assigned" : dateTime));
        System.out.println("Room: " + room);
        System.out.println("Students count: " + students.size());
    }

    public String getTopic() {
        return topic;
    }

    public LessonType getType() {
        return type;
    }

    public Course getCourse() {
        return course;
    }

    public Teacher getInstructor() {
        return instructor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getRoom() {
        return room;
    }

    public List<Student> getStudents() {
        return students;
    }

    public Map<Student, Boolean> getAttendance() {
        return attendance;
    }

    @Override
    public String toString() {
        return "[" + (dateTime == null ? "No date" : dateTime) + "] " +
                type + " | " +
                (course == null ? "No course" : course.getCode()) +
                " | " + topic +
                " | Teacher: " + (instructor == null ? "Not assigned" : instructor.getFullName()) +
                " | Room: " + room;
    }
}