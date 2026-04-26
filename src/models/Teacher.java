package models;

import java.util.ArrayList;
import java.util.List;

import enums.TeacherTitle;

public class Teacher extends Employee implements Researcher {
    private TeacherTitle title;
    private List<Course> courses = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private int hIndex;

    public Teacher(String id, String username, String password, String fullName, double salary, 
        TeacherTitle title, int hIndex) {
            super(id, username, password, fullName, salary);
            this.title = title;
            this.hIndex = hIndex;
    }

    public void putMark(Student student, Course course, Mark mark) {
        student.addMark(course, mark);
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
        System.out.println("Teacher menu");
    }
    
}
