package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private Employee sender;
    private Employee recipient;
    private String subject;
    private String description;
    private LocalDateTime createdAt;
    private boolean resolved;

    public Complaint(Employee sender, Employee recipient, String subject, String description) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    public Employee getSender() {
        return sender;
    }

    public Employee getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void resolve() {
        this.resolved = true;
    }

    @Override
    public String toString() {
        return "[" + createdAt + "] Complaint from " + sender.getFullName() +
                " to " + recipient.getFullName() +
                "\nSubject: " + subject +
                "\nDescription: " + description +
                "\nStatus: " + (resolved ? "RESOLVED" : "OPEN");
    }
}