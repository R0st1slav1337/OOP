package models;

import exceptions.LowHIndexException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User implements Researcher {
    private static final long serialVersionUID = 1L;

    private int year;
    private double gpa;
    private int totalCredits;
    private int hIndex;
    private Researcher supervisor;
    private String major;

    private Map<Course, Mark> marks = new HashMap<>();
    private List<Course> courses = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private List<ResearchProject> researchProjects = new ArrayList<>();
    

    public Student(String id, String username, String password, String fullName,
                   int year, String major, double gpa, int hIndex) {
        super(id, username, password, fullName);
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.hIndex = hIndex;
    }

    // This method allows the student to request registration for a course. It checks if the course exists, 
    // if the student is already registered, if the credit limit is exceeded, if the course is intended for the student's major/year, 
    // and if the student has failed 3 or more courses. If all checks pass, it creates a new Registration object and returns it.
    public Registration requestRegistration(Course course) {
        String blockReason = getRegistrationBlockReason(course);

        if (blockReason != null) {
            Database.getInstance().addLog(
                "STUDENT: " + getFullName() +
                        " failed to request registration for course " +
                        course.getName() +
                        " because " + blockReason + "."
            );

            System.out.println("Cannot register: " + blockReason + ".");

            if (blockReason.equals("prerequisites are not passed")) {
                course.printPrerequisites();
            }

            return null;
        }

        Database.getInstance().addLog(
            "STUDENT: " + getFullName() +
            " requested registration for course " + course.getName()
        );

        return new Registration(this, course);
    }

    public void dropCourse(Course course) {
        if (course == null) {
            System.out.println("Course does not exist.");
            return;
        }

        if (courses == null || !courses.contains(course)) {
            System.out.println("You are not registered for this course.");
            return;
        }

        if (marks != null && marks.containsKey(course)) {
            System.out.println("Cannot drop course: mark has already been assigned.");

            Database.getInstance().addLog(
                    "STUDENT: " + getFullName() +
                            " failed to drop course " + course.getName() +
                            " because mark has already been assigned."
            );

            return;
        }

        courses.remove(course);
        totalCredits -= course.getCredits();

        if (totalCredits < 0) {
            totalCredits = 0;
        }

        course.removeStudent(this);

        Database.getInstance().addLog(
                "STUDENT: " + getFullName() +
                        " dropped course " + course.getName()
        );

        System.out.println("Course dropped successfully.");
    }
    
    void addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            totalCredits += course.getCredits();
        }
    }

    public String getRegistrationBlockReason(Course course) {
        if (course == null) {
            return "course does not exist";
        }

        if (courses.contains(course)) {
            return "already registered";
        }

        if (course.isFull()) {
            return "course is full";
        }

        if (totalCredits + course.getCredits() > 21) {
            return "credit limit exceeded";
        }

        if (!course.isAvailableFor(this)) {
            return "course is not intended for student's major/year";
        }

        if (getFailedCoursesCount() >= 3) {
            return "student has failed 3 or more courses";
        }

        if (!hasPassedPrerequisites(course)) {
            return "prerequisites are not passed";
        }

        return null;
    }

    public void viewAvailableCourses(Database database) {
        if (database == null || database.getCourses().isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        boolean found = false;

        System.out.println("Available courses for " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (Course course : database.getCourses()) {
            String blockReason = getRegistrationBlockReason(course);

            if (blockReason == null) {
                System.out.println(course);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No available courses found.");
        }

        Database.getInstance().addLog(
                "STUDENT: " + getFullName() + " viewed available courses."
        );
    }

    public void viewCoursesWithRegistrationStatus(Database database) {
        if (database == null || database.getCourses().isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        System.out.println("Courses and registration availability for " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (Course course : database.getCourses()) {
            String blockReason = getRegistrationBlockReason(course);

            System.out.println(course);

            if (blockReason == null) {
                System.out.println("Status: AVAILABLE");
            } else {
                System.out.println("Status: NOT AVAILABLE - " + blockReason);
            }

            System.out.println("--------------------------------");
        }
    }

    public boolean needsSupervisor() {
        return year == 4;
    }

    public Researcher getSupervisor() {
        return supervisor;
    }

    // This method allows the student to set a research supervisor, but only if they are a 4th year student and the 
    // supervisor has an h-index of at least 3
    public void setSupervisor(Researcher supervisor) throws LowHIndexException {
        if (!needsSupervisor()) {
            System.out.println("Only 4th year students need a research supervisor.");
            Database.getInstance().addLog(
                 "STUDENT: " + getFullName() +
                            " tried to assign supervisor, but student is not 4th year."
            );
            return;
        }

        if (supervisor == null) {
            System.out.println("Supervisor does not exist.");
            return;
        }

        if (supervisor.getHIndex() < 3) {
            Database.getInstance().addLog(
                    "STUDENT: " + getFullName() +
                            " failed to assign supervisor because supervisor h-index is lower than 3."
            );

            throw new LowHIndexException("Supervisor h-index must be at least 3.");
        }

        this.supervisor = supervisor;

        User supervisorUser = (User) supervisor;

        Database.getInstance().addLog(
                "STUDENT: " + getFullName() +
                        " assigned research supervisor " + supervisorUser.getFullName()
        );

        System.out.println("Supervisor assigned successfully: " + supervisorUser.getFullName());
    }

    public void addMark(Course course, Mark mark) {
        if (!courses.contains(course)) {
            System.out.println("Cannot add mark: student is not registered for this course.");
            return;
        }
        
        marks.put(course, mark);
    }

    public void viewMarks() {
        for (Course course : marks.keySet()) {
            System.out.println(course + " -> " + marks.get(course));
        }
    }

    public void viewCourses() {
        for (Course course : courses) {
            System.out.println(course);
        }
    }

    public void viewAttendance() {
        if (courses == null || courses.isEmpty()) {
            System.out.println("You are not registered for any courses.");
            return;
        }

        System.out.println("Attendance of " + getFullName());
        System.out.println("--------------------------------");

        boolean hasAnyLessons = false;

        for (Course course : courses) {
            int totalLessons = 0;
            int attendedLessons = 0;

            for (Lesson lesson : course.getLessons()) {
                if (lesson.hasStudent(this)) {
                    totalLessons++;
                    hasAnyLessons = true;

                    if (lesson.isPresent(this)) {
                        attendedLessons++;
                    }
                }
            }

            if (totalLessons > 0) {
                double percentage = (attendedLessons * 100.0) / totalLessons;

                System.out.println("Course: " + course.getName());
                System.out.println("Attended: " + attendedLessons + "/" + totalLessons);
                System.out.printf("Attendance: %.2f%%%n", percentage);
                System.out.println("--------------------------------");
            }
        }

        if (!hasAnyLessons) {
            System.out.println("No lessons found.");
        }

        Database.getInstance().addLog(
                "STUDENT: " + getFullName() + " viewed attendance."
        );
    }

    // This method allows the student to view their transcript, which includes all courses and marks
    public Transcript getTranscript() {
        Transcript transcript = new Transcript(this);

        for (Course course : marks.keySet()) {
            transcript.addMark(course, marks.get(course));
        }

        return transcript;
    }

    // Get the number of failed courses (courses with marks below 50)
    public int getFailedCoursesCount() {
        int count = 0;

        for (Mark mark : marks.values()) {
            if (!mark.isPassed()) {
                count++;
            }
        }

        return count;
    }

    // This method allows the students to rate their teachers for the courses they are registered in
    public void rateTeacher(Teacher teacher, Course course, int ratingValue, String comment) {
        if (teacher == null) {
            System.out.println("Teacher does not exist.");
            return;
        }

        if (course == null) {
            System.out.println("Course does not exist.");
            return;
        }

        if (!courses.contains(course)) {
            System.out.println("Cannot rate teacher: student is not registered for this course.");

            Database.getInstance().addLog(
                    "STUDENT: " + getFullName() +
                            " failed to rate teacher " + teacher.getFullName() +
                            " because student is not registered for course " + course.getName()
            );

            return;
        }

        // Check if the teacher teaches this course
        if (!course.getInstructors().contains(teacher)) {
            System.out.println("Cannot rate teacher: this teacher does not teach this course.");

            Database.getInstance().addLog(
                    "STUDENT: " + getFullName() +
                            " failed to rate teacher " + teacher.getFullName() +
                            " because teacher does not teach course " + course.getName()
            );

            return;
        }

        // Check if the student has already rated this teacher for this course
        if (teacher.hasRatingFrom(this, course)) {
            System.out.println("You have already rated this teacher for this course.");

         Database.getInstance().addLog(
                    "STUDENT: " + getFullName() +
                            " failed to rate teacher " + teacher.getFullName() +
                            " because rating already exists for course " + course.getName()
            );

            return;
        }

        TeacherRating rating = new TeacherRating(this, teacher, course, ratingValue, comment);
        teacher.addRating(rating);

        Database.getInstance().addLog(
                "STUDENT: " + getFullName() +
                        " rated teacher " + teacher.getFullName() +
                        " for course " + course.getName() +
                        ". Rating: " + ratingValue + "/5"
        );

        System.out.println("Teacher was rated successfully.");
    }

    // This method allows the student to view the schedule of all lessons for the courses they are registered in
    public void viewSchedule() {
        System.out.println("Schedule of student " + getFullName() + ":");
        System.out.println("--------------------------------");

        boolean hasLessons = false;

        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getStudents().contains(this)) {
                    System.out.println(lesson);
                    hasLessons = true;
                }
            }
        }

        if (!hasLessons) {
            System.out.println("No lessons found.");
        }
    }

    // This method allows the student to view the schedule of a specific course they are registered in
    public void viewLessonsForCourse(Course course) {
        if (course == null) {
            System.out.println("Course does not exist.");
            return;
        }

        if (!courses.contains(course)) {
            System.out.println("Student is not registered for this course.");
            return;
        }

        course.printSchedule();
    }

    public void viewTeachersForCourse(Course course) {
        if (course == null) {
            System.out.println("Course does not exist.");
            return;
        }

        if (!courses.contains(course)) {
            System.out.println("You are not registered for this course.");
            return;
        }

        if (course.getInstructors() == null || course.getInstructors().isEmpty()) {
            System.out.println("No teachers assigned to this course.");
            return;
        }

        System.out.println("Teachers for course: " + course.getName());
        System.out.println("--------------------------------");

        for (Teacher teacher : course.getInstructors()) {
            System.out.println("Name: " + teacher.getFullName());
            System.out.println("Department: " + teacher.getDepartment());
            System.out.println("Title: " + teacher.getTitle());
            System.out.println("H-index: " + teacher.getHIndex());
            System.out.printf("Average rating: %.2f/5%n", teacher.getAverageRating());
            System.out.println("--------------------------------");
        }

        Database.getInstance().addLog(
                "STUDENT: " + getFullName() +
                    " viewed teacher info for course " + course.getName()
        );
    }

    public void printStudentDetails() {
        System.out.println("=== Student Details ===");
        System.out.println("Full name: " + getFullName());
        System.out.println("Username: " + getUsername());
        System.out.println("Year: " + year);
        System.out.println("Major: " + major);
        System.out.printf("GPA: %.2f%n", calculateGpa());
        System.out.println("Total credits: " + totalCredits);
        System.out.println("Failed courses: " + getFailedCoursesCount());

        System.out.println();
        System.out.println("Courses:");
        if (courses == null || courses.isEmpty()) {
            System.out.println("- No courses");
        } else {
            for (Course course : courses) {
                System.out.println("- " + course.getCode() + " - " + course.getName());
            }
        }

        System.out.println();
        System.out.println("Marks:");
        if (marks == null || marks.isEmpty()) {
            System.out.println("- No marks");
        } else {
            for (Course course : marks.keySet()) {
                System.out.println("- " + course.getCode() + " - " + course.getName() + ": " + marks.get(course));
            }
        }

        System.out.println();
        System.out.println("Research:");
        System.out.println("H-index: " + getHIndex());
        System.out.println("Research papers: " + getResearchPapers().size());
        System.out.println("Research projects: " + getResearchProjects().size());

        if (supervisor != null) {
            User supervisorUser = (User) supervisor;
            System.out.println("Supervisor: " + supervisorUser.getFullName());
        } else {
            System.out.println("Supervisor: not assigned");
        }
    }

    public boolean hasPassedCourse(Course course) {
        if (course == null) {
            return false;
        }

        Mark mark = marks.get(course);

        return mark != null && mark.isPassed();
    }

    public boolean hasPassedPrerequisites(Course course) {
        if (course == null) {
            return false;
        }

        for (Course prerequisite : course.getPrerequisites()) {
            if (!hasPassedCourse(prerequisite)) {
                return false;
            }
        }

        return true;
    }

    public int getYear() {
        return year;
    }

    public String getMajor() {
        return major;
    }

    public double getGpa() {
        return gpa;
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Map<Course, Mark> getMarks() {
        return marks;
    }

    // GPA is calculated as the weighted average of the grade points, where the weights are the course credits
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

}