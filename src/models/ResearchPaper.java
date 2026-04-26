package models;

import java.time.LocalDate;
import java.util.List;

public class ResearchPaper implements Comparable<ResearchPaper> {
    private String title;
    private List<String> authors;
    private String journal;
    private int pages;
    private int citations;
    private LocalDate publicationDate;
    private String doi;

    public ResearchPaper(String title, List<String> authors, String journal,
                         int pages, int citations, LocalDate publicationDate, String doi) {
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.pages = pages;
        this.citations = citations;
        this.publicationDate = publicationDate;
        this.doi = doi;
    }

    public int getPages() {
        return pages;
    }

    public int getCitations() {
        return citations;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    @Override
    public int compareTo(ResearchPaper other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return title + " | Citations: " + citations + " | Date: " + publicationDate;
    }
}