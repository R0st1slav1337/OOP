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
    private int capacity;

    private List<Teacher> instructors = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Lesson> lessons = new ArrayList<>();
    private List<Course> prerequisites = new ArrayList<>();

    // default constructor
    public Course(String code, String name, int credits) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.intendedMajor = "ALL";
        this.intendedYear = 0;
        this.capacity = 30;
    }

    public Course(String code, String name, int credits, String intendedMajor, int intendedYear) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.intendedMajor = intendedMajor;
        this.intendedYear = intendedYear;
        this.capacity = 30;
    }

    public Course(String code, String name, int credits, String intendedMajor, int intendedYear, int capacity) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.intendedMajor = intendedMajor;
        this.intendedYear = intendedYear;

        if (capacity <= 0) {
            this.capacity = 30;
        } else {
            this.capacity = capacity;
        }
    }

    public void addInstructor(Teacher teacher) {
        if (!instructors.contains(teacher)) {
            instructors.add(teacher);
        }
    }

    public void addStudent(Student student) {
        if (student == null) {
            return;
        }

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

    public void removeStudent(Student student) {
        if (student == null) {
            return;
        }

        if (students == null) {
            return;
        }

        if (students.remove(student)) {
            if (lessons != null) {
                for (Lesson lesson : lessons) {
                    lesson.removeStudent(student);
                }
            }
        }
    }

    public boolean isAvailableFor(Student student) {
        if (student == null) {
            return false;
        }
        
        String major = intendedMajor;

        if (major == null || major.isBlank()) {
            major = "ALL";
        }

        boolean majorMatches = major.equalsIgnoreCase("ALL")
                || major.equalsIgnoreCase(student.getMajor());

        boolean yearMatches = intendedYear == 0
                || intendedYear == student.getYear();

        return majorMatches && yearMatches;
    }

    public void addLesson(Lesson lesson) {
        if (lesson == null) {
            System.out.println("Lesson does not exist.");
            return;
        }
        
        if (lessons == null) {
            lessons = new ArrayList<>();
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

    public void printAttendanceReport() {
        if (students == null || students.isEmpty()) {
            System.out.println("No students registered for course " + name);
            return;
        }

        if (lessons == null || lessons.isEmpty()) {
            System.out.println("No lessons for course " + name);
            return;
        }

        System.out.println("Attendance report for course: " + name);
        System.out.println("--------------------------------");

        for (Student student : students) {
            int totalLessons = 0;
            int attendedLessons = 0;

            for (Lesson lesson : lessons) {
                if (lesson.hasStudent(student)) {
                    totalLessons++;

                    if (lesson.isPresent(student)) {
                        attendedLessons++;
                    }
                }
            }

            double percentage = totalLessons == 0
                    ? 0.0
                    : (attendedLessons * 100.0) / totalLessons;

            System.out.println("Student: " + student.getFullName());
            System.out.println("Attended: " + attendedLessons + "/" + totalLessons);
            System.out.printf("Attendance: %.2f%%%n", percentage);
            System.out.println("--------------------------------");
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

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        if (students == null) {
            return false;
        }

        return students.size() >= capacity;
    }

    public int getAvailablePlaces() {
        if (students == null) {
            return capacity;
        }

        return capacity - students.size();
    }

    public void addPrerequisite(Course course) {
        if (course == null) {
            System.out.println("Prerequisite course does not exist.");
            return;
        }

        if (course == this) {
            System.out.println("Course cannot be prerequisite of itself.");
            return;
        }

        if (prerequisites == null) {
            prerequisites = new ArrayList<>();
        }

        if (!prerequisites.contains(course)) {
            prerequisites.add(course);
        }
    }

    public List<Course> getPrerequisites() {
        if (prerequisites == null) {
            prerequisites = new ArrayList<>();
        }

        return prerequisites;
    }

    public boolean hasPrerequisites() {
        return prerequisites != null && !prerequisites.isEmpty();
    }

    public void printPrerequisites() {
        if (prerequisites == null || prerequisites.isEmpty()) {
            System.out.println("No prerequisites for course " + name);
            return;
        }

        System.out.println("Prerequisites for " + name + ":");

        for (Course course : prerequisites) {
            System.out.println("- " + course.getCode() + " - " + course.getName());
        }
    }

    public void printCourseDetails() {
        String major = intendedMajor;

        if (major == null || major.isBlank()) {
            major = "ALL";
        }

        System.out.println("=== Course Details ===");
        System.out.println("Code: " + code);
        System.out.println("Name: " + name);
        System.out.println("Credits: " + credits);
        System.out.println("Intended major: " + major);
        System.out.println("Intended year: " + (intendedYear == 0 ? "ALL" : intendedYear));
        System.out.println("Capacity: " + capacity);
        System.out.println("Available places: " + getAvailablePlaces());

        System.out.println();
        System.out.println("Prerequisites:");
        if (prerequisites == null || prerequisites.isEmpty()) {
            System.out.println("- No prerequisites");
        } else {
            for (Course prerequisite : prerequisites) {
                System.out.println("- " + prerequisite.getCode() + " - " + prerequisite.getName());
            }
        }

        System.out.println();
        System.out.println("Instructors:");
        if (instructors == null || instructors.isEmpty()) {
            System.out.println("- No instructors assigned");
        } else {
            for (Teacher teacher : instructors) {
                System.out.println("- " + teacher.getFullName() +
                        " | " + teacher.getTitle() +
                        " | department: " + teacher.getDepartment() +
                        " | rating: " + String.format("%.2f", teacher.getAverageRating()) + "/5");
            }
        }

        System.out.println();
        System.out.println("Students registered: " + (students == null ? 0 : students.size()));

        System.out.println();
        System.out.println("Lessons:");
        if (lessons == null || lessons.isEmpty()) {
            System.out.println("- No lessons created");
        } else {
            for (Lesson lesson : lessons) {
                System.out.println("- " + lesson);
            }
        }
    }

    @Override
    public String toString() {
        String major = intendedMajor;

        if (major == null || major.isBlank()) {
            major = "ALL";
        }

        int prerequisitesCount = prerequisites == null ? 0 : prerequisites.size();

        return code + " - " + name +
                " (" + credits + " credits)" +
                " | major: " + major +
                " | year: " + (intendedYear == 0 ? "ALL" : intendedYear) +
                " | capacity: " + capacity +
                " | available places: " + getAvailablePlaces() +
                " | prerequisites: " + prerequisitesCount;
    }
    
}