package models;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<User> users = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<ResearchProject> researchProjects = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void addResearchProject(ResearchProject project) {
        researchProjects.add(project);
    }

    public void addResearchPaper(ResearchPaper paper) {
        researchPapers.add(paper);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<ResearchProject> getResearchProjects() {
        return researchProjects;
    }

    public List<ResearchPaper> getResearchPapers() {
        return researchPapers;
    }
    
}