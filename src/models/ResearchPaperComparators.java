package models;

import java.util.Comparator;

public class ResearchPaperComparators {
    public static final Comparator<ResearchPaper> BY_DATE =
            Comparator.comparing(ResearchPaper::getPublicationDate);

    public static final Comparator<ResearchPaper> BY_CITATIONS =
            Comparator.comparing(ResearchPaper::getCitations).reversed();

    public static final Comparator<ResearchPaper> BY_PAGES =
            Comparator.comparing(ResearchPaper::getPages).reversed();
}