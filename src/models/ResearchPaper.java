package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResearchPaper implements Comparable<ResearchPaper>, Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private List<User> authors;
    private String journal;
    private int pages;
    private int citations;
    private LocalDate publicationDate;
    private String doi;

    public ResearchPaper(String title, List<? extends User> authors, String journal,
                         int pages, int citations, LocalDate publicationDate, String doi) {
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.journal = journal;
        this.pages = pages;
        this.citations = citations;
        this.publicationDate = publicationDate;
        this.doi = doi;
    }

    public String getTitle() {
        return title;
    }

    public List<User> getAuthors() {
        return authors;
    }

    public String getJournal() {
        return journal;
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

    public String getDoi() {
        return doi;
    }

    @Override
    public int compareTo(ResearchPaper other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return title + " | Citations: " + citations + " | Date: " + publicationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper that = (ResearchPaper) o;
        return Objects.equals(doi, that.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }
}