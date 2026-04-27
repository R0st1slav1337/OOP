package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Database {
    private List<User> users = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<ResearchProject> researchProjects = new ArrayList<>();
    private List<ResearchPaper> researchPapers = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
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

    public void printAllResearchPapers(Comparator<ResearchPaper> comparator) {
        researchPapers.stream().sorted(comparator).forEach(System.out::println);
    }

    public Researcher getTopCitedResearcher() {
        Researcher topResearcher = null;
        int maxCitations = -1;

        for (User user: users) {
            if (user instanceof Researcher researcher) {
                int totalCitations = researcher.getResearchPapers().stream()
                .mapToInt(ResearchPaper::getCitations).sum();

                if (totalCitations > maxCitations) {
                    maxCitations = totalCitations;
                    topResearcher = researcher;
                }
            }
        }

        return topResearcher;
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