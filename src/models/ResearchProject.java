package models;

import exceptions.NotResearcherException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topic;
    private List<ResearchPaper> publishedPapers = new ArrayList<>();
    private List<Researcher> participants = new ArrayList<>();

    public ResearchProject(String topic) {
        this.topic = topic;
    }

    public void addParticipant(User user) throws NotResearcherException {
        if (!(user instanceof Researcher)) {
            throw new NotResearcherException("Only researchers can join research project.");
        }

        Researcher researcher = (Researcher) user;

        if (!participants.contains(researcher)) {
            participants.add(researcher);
        }
    }

    public void addPaper(Researcher researcher, ResearchPaper paper) {
        if (researcher == null) {
            System.out.println("Researcher does not exist.");
            return;
        }

        if (paper == null) {
            System.out.println("Research paper does not exist.");
            return;
        }

        if (participants == null) {
            participants = new ArrayList<>();
        }

        if (publishedPapers == null) {
            publishedPapers = new ArrayList<>();
        }

        if (!participants.contains(researcher)) {
            System.out.println("Cannot add paper: researcher is not a participant of this project.");

            User user = (User) researcher;

            Database.getInstance().addLog(
                    "RESEARCH: " + user.getFullName() +
                            " failed to add paper '" + paper.getTitle() +
                            "' to project '" + topic +
                            "' because researcher is not a participant."
            );

            return;
        }

        if (!publishedPapers.contains(paper)) {
            publishedPapers.add(paper);

            User user = (User) researcher;

            Database.getInstance().addLog(
                    "RESEARCH: " + user.getFullName() +
                            " added paper '" + paper.getTitle() +
                            "' to project '" + topic + "'"
            );

            System.out.println("Paper added to research project successfully.");
        } else {
            System.out.println("This paper is already added to the project.");
        }
    }

    public void printParticipants() {
        System.out.println("Participants of project: " + topic);

        for (Researcher researcher : participants) {
            User user = (User) researcher;
            System.out.println("- " + user.getFullName() + ", h-index: " + researcher.getHIndex());
        }
    }

    public void printPublishedPapers() {
        if (publishedPapers == null || publishedPapers.isEmpty()) {
            System.out.println("No papers published in project: " + topic);
            return;
        }

        System.out.println("Published papers in project: " + topic);
        System.out.println("--------------------------------");

        for (ResearchPaper paper : publishedPapers) {
            System.out.println(paper);
        }
    }

    public String getTopic() {
        return topic;
    }

    public List<ResearchPaper> getPublishedPapers() {
        return publishedPapers;
    }

    public List<Researcher> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return "Research project: " + topic;
    }
}