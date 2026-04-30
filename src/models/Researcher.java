package models;

import exceptions.NotResearcherException;

import java.util.Comparator;
import java.util.List;

public interface Researcher {
    int getHIndex();

    List<ResearchPaper> getResearchPapers();

    List<ResearchProject> getResearchProjects();

    void addResearchPaper(ResearchPaper paper);

    void addResearchProject(ResearchProject project);

    default void joinProject(ResearchProject project) throws NotResearcherException {
        project.addParticipant((User) this);
        addResearchProject(project);
    }

    default void printPapers(Comparator<ResearchPaper> comparator) {
        getResearchPapers().stream().sorted(comparator).forEach(System.out::println);
    }

    default int getTotalCitations() {
        return getResearchPapers().stream().mapToInt(ResearchPaper::getCitations).sum();
    }

    default int calculateHIndex() {
        List<Integer> citations = getResearchPapers().stream().map(ResearchPaper::getCitations)
        .sorted(Comparator.reverseOrder()).toList();

        int hIndex = 0;

        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) {
                hIndex = i + 1;
            } else {
                break;
            }
        }

        return hIndex;
    }
}
