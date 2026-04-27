package models;

import exceptions.NotResearcherException;

import java.util.ArrayList;
import java.util.List;

public class ResearchProject {
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

        participants.add((Researcher) user);
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
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