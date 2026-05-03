package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class News implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private Manager author;

    public News(String title, String content, Manager author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public Manager getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "[" + publishedAt + "] " + title +
                "\nBy: " + author.getFullName() +
                "\n" + content;
    }
    
}
