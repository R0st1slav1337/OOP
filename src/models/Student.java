package models;

import exceptions.LowHIndexException;

import java.util.*;

public class Student extends User implements Researcher {
    private int year;
    private double gpa;
    private int totalCredits;
    private Researcher supervisor;

    private Map<Course, Mark> marks = new HashMap<>();
    private List<Course> courses = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private int hIndex;

    public Student(String id, String username, String password, String fullName,
                   int year, double gpa, int hIndex) {
        super(id, username, password, fullName);
        this.year = year;
        this.gpa = gpa;
        this.hIndex = hIndex;
    }

    public void registerCourse(Course course) {
        if (totalCredits + course.getCredits() <= 21) {
            courses.add(course);
            totalCredits += course.getCredits();
        } else {
            System.out.println("Cannot register: credit limit exceeded.");
        }
    }

    public void setSupervisor(Researcher supervisor) throws LowHIndexException {
        if (year == 4 && supervisor.getHIndex() < 3) {
            throw new LowHIndexException("Supervisor h-index must be at least 3.");
        }

        this.supervisor = supervisor;
    }

    public void addMark(Course course, Mark mark) {
        marks.put(course, mark);
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
    }

    @Override
    public void showMenu() {
        System.out.println("Student menu");
    }
}