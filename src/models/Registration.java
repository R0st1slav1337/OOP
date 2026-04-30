package models;

import java.io.Serializable;

import enums.RegistrationStatus;

public class Registration implements Serializable {
    private static final long serialVersionUID = 1L;

    private Student student;
    private Course course;
    private RegistrationStatus status;

    public Registration(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.status = RegistrationStatus.PENDING;
    }

    public void approve() {
        this.status = RegistrationStatus.APPROVED;
    }

    public void reject() {
        this.status = RegistrationStatus.REJECTED;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return student.getFullName() + " -> " + course + " [" + status + "]";
    }
    
}