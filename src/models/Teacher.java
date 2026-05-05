package models;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import enums.TeacherTitle;
import enums.LessonType;

public class Teacher extends Employee implements Researcher {
    private TeacherTitle title;
    private List<Course> courses = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private int hIndex;
    private List<ResearchProject> researchProjects = new ArrayList<>();
    private List<TeacherRating> ratings = new ArrayList<>();

    public Teacher(String id, String username, String password, String fullName, 
                    double salary, String department, TeacherTitle title, int hIndex) {
            super(id, username, password, fullName, salary, department);
            this.title = title;
            this.hIndex = hIndex;
    }

    public void assignCourse(Course course) {
        courses.add(course);
        course.addInstructor(this);
    }

    public void putMark(Student student, Course course, Mark mark) {
        if (!courses.contains(course)) {
            System.out.println("Cannot put mark: teacher is not assigned to this course.");
            
            Database.getInstance().addLog(
                "TEACHER: " + getFullName() +
                " failed to put mark because he/she is not assigned to course " + course.getName()
            
            );
            return;
        }

        if (!course.getStudents().contains(student)) {
            System.out.println("Cannot put mark: student is not registered for this course.");
            
            Database.getInstance().addLog(
                "TEACHER: " + getFullName() +
                " failed to put mark because student " + student.getFullName() +
                " is not registered for course " + course.getName()
            
            );
            return;
        }
        
        student.addMark(course, mark);
        System.out.println("Mark was successfully added for " + student.getFullName());
        
        Database.getInstance().addLog(
            "TEACHER: " + getFullName() +
            " put mark for student " + student.getFullName() +
            " in course " + course.getName() +
            ". Total: " + mark.getTotal()
        );
    }

    public void viewCourses() {
        for (Course course : courses) {
            System.out.println(course);
        }
    }

    public void addRating(TeacherRating rating) {
        if (ratings == null) {
            ratings = new ArrayList<>();
        }

        if (!hasRatingFrom(rating.getStudent(), rating.getCourse())) {
            ratings.add(rating);
        }
    }

    public boolean hasRatingFrom(Student student, Course course) {
        if (ratings == null) {
            ratings = new ArrayList<>();
        }

        for (TeacherRating rating : ratings) {
            if (rating.getStudent().equals(student) && rating.getCourse().equals(course)) {
                return true;
            }
        }

        return false;
    }

    public double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;

        for (TeacherRating rating : ratings) {
            sum += rating.getRating();
        }

        return sum / ratings.size();
    }

    public void printRatings() {
        if (ratings == null || ratings.isEmpty()) {
            System.out.println("No ratings for teacher " + getFullName());
            return;
        }

        System.out.println("Ratings for teacher " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (TeacherRating rating : ratings) {
            System.out.println(rating);
        }

        System.out.printf("Average rating: %.2f/5%n", getAverageRating());
    }

    public List<TeacherRating> getRatings() {
        return ratings;
    }

    private boolean hasLessonAt(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }

        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                if (dateTime.equals(lesson.getDateTime())) {
                    return true;
                }
            }
        }

        return false;
    }

    public Lesson createLesson(Course course, String topic, LessonType type, LocalDateTime dateTime, String room) {
        if (course == null) {
            System.out.println("Course does not exist.");
            return null;
        }

        if (!courses.contains(course)) {
            System.out.println("Cannot create lesson: teacher is not assigned to this course.");

            Database.getInstance().addLog(
                    "TEACHER: " + getFullName() +
                            " failed to create lesson because teacher is not assigned to course " +
                            course.getName()
            );

            return null;
        }

        if (hasLessonAt(dateTime)) {
            System.out.println("Cannot create lesson: teacher already has a lesson at this time.");

            Database.getInstance().addLog(
                    "TEACHER: " + getFullName() +
                            " failed to create lesson because of schedule conflict at " + dateTime
            );

            return null;
        }

        Lesson lesson = new Lesson(topic, type, course, this, dateTime, room);
        course.addLesson(lesson);

        Database.getInstance().addLog(
                "TEACHER: " + getFullName() +
                        " created " + type +
                        " lesson for course " + course.getName() +
                        ". Topic: " + topic
        );

        System.out.println("Lesson created successfully.");

        return lesson;
    }

    public void markAttendance(Lesson lesson, Student student, boolean present) {
        if (lesson == null) {
            System.out.println("Lesson does not exist.");
            return;
        }

        if (lesson.getInstructor() != this) {
            System.out.println("Cannot mark attendance: this teacher is not instructor of this lesson.");

            Database.getInstance().addLog(
                    "TEACHER: " + getFullName() +
                            " failed to mark attendance because teacher is not instructor of lesson " +
                            lesson.getTopic()
            );

            return;
        }

        lesson.markAttendance(student, present);

        Database.getInstance().addLog(
                "TEACHER: " + getFullName() +
                        " marked attendance for student " + student.getFullName() +
                        " in lesson " + lesson.getTopic() +
                        ": " + (present ? "Present" : "Absent")
        );
    }

    public void viewSchedule() {
        System.out.println("Schedule of teacher " + getFullName() + ":");
        System.out.println("--------------------------------");

        boolean hasLessons = false;

        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getInstructor() == this) {
                    System.out.println(lesson);
                    hasLessons = true;
                }
            }
        }

        if (!hasLessons) {
            System.out.println("No lessons found.");
        }
    }

    public void viewStudentsForCourse(Course course) {
        if (course == null) {
            System.out.println("Course does not exist.");
            return;
        }

        if (!courses.contains(course)) {
            System.out.println("You are not assigned to this course.");
            return;
        }

        if (course.getStudents() == null || course.getStudents().isEmpty()) {
            System.out.println("No students registered for this course.");
            return;
        }

        System.out.println("Students for course: " + course.getName());
        System.out.println("--------------------------------");

        for (Student student : course.getStudents()) {
            System.out.println("Name: " + student.getFullName());
            System.out.println("Username: " + student.getUsername());
            System.out.println("Major: " + student.getMajor());
            System.out.println("Year: " + student.getYear());
            System.out.printf("GPA: %.2f%n", student.calculateGpa());
            System.out.println("--------------------------------");
        }

        Database.getInstance().addLog(
            "TEACHER: " + getFullName() +
                    " viewed students for course " + course.getName()
        );
    }

    public TeacherTitle getTitle() {
        return title;
    }

    public List<Course> getCourses() {
        return courses;
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    @Override
    public List<ResearchPaper> getResearchPapers() {
        return researchPapers;
    }

    @Override
    public void addResearchPaper(ResearchPaper paper) {
        researchPapers.add(paper);
        Database.getInstance().addLog(
            "RESEARCH: " + getFullName() +
            " added research paper '" + paper.getTitle() + "'"
        );
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return researchProjects;
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (!researchProjects.contains(project)) {
            researchProjects.add(project);
        }

        Database.getInstance().addLog(
            "RESEARCH: " + getFullName() +
            " joined research project '" + project.getTopic() + "'"
        );
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public void showMenu() {
        System.out.println("Teacher menu");
    }
    
}
