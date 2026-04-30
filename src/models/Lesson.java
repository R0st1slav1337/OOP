package models;

import enums.LessonType;
import java.io.Serializable;

public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topic;
    private LessonType type;
    private Course course;

    public Lesson(String topic, LessonType type, Course course) {
        this.topic = topic;
        this.type = type;
        this.course = course;
    }

    @Override
    public String toString() {
        return type + ": " + topic;
    }
}