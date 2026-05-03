package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private Employee sender;
    private Employee recipient;
    private String text;
    private LocalDateTime sentAt;
    private boolean read;

    public Message(Employee sender, Employee recipient, String text) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.sentAt = LocalDateTime.now();
        this.read = false;
    }

    public Employee getSender() {
        return sender;
    }

    public Employee getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "[" + sentAt + "] From: " + sender.getFullName() +
                " | To: " + recipient.getFullName() +
                " | " + (read ? "READ" : "UNREAD") +
                "\n" + text;
    }
}