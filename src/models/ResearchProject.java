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

    public void addParticipant(Object person) throws NotResearcherException {
        if (!(person instanceof Researcher)) {
            throw new NotResearcherException("Only researchers can join research project.");
        }

        participants.add((Researcher) person);
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
    }

    @Override
    public String toString() {
        return "Research project: " + topic;
    }
}