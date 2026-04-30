package models;

import java.util.ArrayList;
import java.util.List;

import enums.TeacherTitle;

public class Teacher extends Employee implements Researcher {
    private TeacherTitle title;
    private List<Course> courses = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private int hIndex;
    private List<ResearchProject> researchProjects = new ArrayList<>();

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
            return;
        }

        if (!course.getStudents().contains(student)) {
            System.out.println("Cannot put mark: student is not registered for this course.");
            return;
        }
        
        student.addMark(course, mark);
        System.out.println("Mark was successfully added for " + student.getFullName());
    }

    public void viewCourses() {
        for (Course course : courses) {
            System.out.println(course);
        }
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
        System.out.println("Teacher menu");
    }
    
}
