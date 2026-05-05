package models;

import java.util.ArrayList;
import java.util.List;

public class ResearchEmployee extends Employee implements Researcher {
    private static final long serialVersionUID = 1L;

    private int hIndex;
    private List<ResearchPaper> researchPapers = new ArrayList<>();
    private List<ResearchProject> researchProjects = new ArrayList<>();

    public ResearchEmployee(String id, String username, String password, String fullName,
                            double salary, String department, int hIndex) {
        super(id, username, password, fullName, salary, department);
        this.hIndex = hIndex;
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    @Override
    public List<ResearchPaper> getResearchPapers() {
        if (researchPapers == null) {
            researchPapers = new ArrayList<>();
        }

        return researchPapers;
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        if (researchProjects == null) {
            researchProjects = new ArrayList<>();
        }

        return researchProjects;
    }

    @Override
    public void addResearchPaper(ResearchPaper paper) {
        if (paper == null) {
            System.out.println("Research paper does not exist.");
            return;
        }

        if (researchPapers == null) {
            researchPapers = new ArrayList<>();
        }

        if (!researchPapers.contains(paper)) {
            researchPapers.add(paper);

            Database.getInstance().addLog(
                    "RESEARCH: " + getFullName() +
                        " added research paper '" + paper.getTitle() + "'"
            );
        }
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (project == null) {
            System.out.println("Research project does not exist.");
            return;
        }

        if (researchProjects == null) {
            researchProjects = new ArrayList<>();
        }

        if (!researchProjects.contains(project)) {
            researchProjects.add(project);

            Database.getInstance().addLog(
                    "RESEARCH: " + getFullName() +
                        " joined research project '" + project.getTopic() + "'"
            );
        }
    }

    @Override
    public String toString() {
        return getFullName() +
            " | username: " + getUsername() +
            " | department: " + getDepartment() +
            " | h-index: " + hIndex +
            " | role: Research Employee";
    }
}