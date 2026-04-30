package models;

import exceptions.LowHIndexException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Comparator;

public class Student extends User implements Researcher {
    private int year;
    private String major;
    private double gpa;
    private int totalCredits;
    private int hIndex;
    private Researcher supervisor;

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

    public Registration requestRegistration(Course course) {
        if (course == null) {
            System.out.println("Course does not exist.");
            return null;
        }

        if (courses.contains(course)) {
            System.out.println("You are already registered for this course.");
            return null;
        }
        
        if (totalCredits + course.getCredits() > 21 ) {
            System.out.println("Cannot register: credit limit exceeded.");
            return null;
        }

        return new Registration(this, course);
    }

    void addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            totalCredits += course.getCredits();
        }
    }

    public void setSupervisor(Researcher supervisor) throws LowHIndexException {
        if (year == 4 && supervisor.getHIndex() < 3) {
            throw new LowHIndexException("Supervisor h-index must be at least 3.");
        }

        this.supervisor = supervisor;
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

    public Transcript getTranscript() {
        Transcript transcript = new Transcript(this);

        for (Course course : marks.keySet()) {
            transcript.addMark(course, marks.get(course));
        }

        return transcript;
    }

    public int getYear() {
        return year;
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

    @Override
    public List<ResearchPaper> getResearchPapers() {
        return researchPapers;
    }

    @Override
    public void addResearchPaper(ResearchPaper paper) {
        researchPapers.add(paper);
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
    }

    @Override
    public void showMenu() {
        System.out.println("Student menu");
    }
}