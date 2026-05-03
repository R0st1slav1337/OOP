package models;

import java.io.Serializable;

public class TeacherRating implements Serializable {
    private static final long serialVersionUID = 1L;

    private Student student;
    private Teacher teacher;
    private Course course;
    private int rating;
    private String comment;

    public TeacherRating(Student student, Teacher teacher, Course course, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        this.student = student;
        this.teacher = teacher;
        this.course = course;
        this.rating = rating;
        this.comment = comment;
    }

    public Student getStudent() {
        return student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Course getCourse() {
        return course;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Student: " + student.getFullName() +
                " | Course: " + course.getName() +
                " | Rating: " + rating + "/5" +
                " | Comment: " + comment;
    }
}