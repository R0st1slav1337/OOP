package models;

import enums.LessonType;

public class Lesson {
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