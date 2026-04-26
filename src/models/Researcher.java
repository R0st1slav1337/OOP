package models;

import java.util.Comparator;
import java.util.List;

public interface Researcher {
    int getHIndex();

    List<ResearchPaper> getResearchPapers();

    void addResearchPaper(ResearchPaper paper);

    default void printPapers(Comparator<ResearchPaper> comparator) {
        getResearchPapers().stream().sorted(comparator).forEach(System.out::println);
    }

}
