package models;

import java.util.ArrayList;
import java.util.List;

public class Employee extends User {
    protected double salary;
    protected String department;

    private List<Message> inbox = new ArrayList<>();
    private List<Message> sentMessages = new ArrayList<>();

    private List<Complaint> receivedComplaints = new ArrayList<>();
    private List<Complaint> sentComplaints = new ArrayList<>();

    public Employee() {}

    public Employee(String id, String username, String password, 
                    String fullName, double salary, String department) {
        super(id, username, password, fullName);
        this.salary = salary;
        this.department = department;
    }

    public void sendMessage(Employee recipient, String text) {
        if (recipient == null) {
            System.out.println("Recipient does not exist.");
            return;
        }

        if (text == null || text.isBlank()) {
            System.out.println("Message cannot be empty.");
            return;
        }

        if (sentMessages == null) {
            sentMessages = new ArrayList<>();
        }

        Message message = new Message(this, recipient, text);

        sentMessages.add(message);
        recipient.receiveMessage(message);

        Database.getInstance().addLog(
                "EMPLOYEE: " + getFullName() +
                        " sent message to " + recipient.getFullName()
        );

        System.out.println("Message sent successfully.");
    }

    public void receiveMessage(Message message) {
        if (inbox == null) {
            inbox = new ArrayList<>();
        }

        inbox.add(message);
    }

    public void viewInbox() {
        if (inbox == null || inbox.isEmpty()) {
            System.out.println("Inbox is empty for " + getFullName());
            return;
        }

        System.out.println("Inbox of " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (Message message : inbox) {
            System.out.println(message);
            message.markAsRead();
            System.out.println("--------------------------------");
        }
    }

    public void viewSentMessages() {
        if (sentMessages == null || sentMessages.isEmpty()) {
            System.out.println("No sent messages for " + getFullName());
            return;
        }

        System.out.println("Sent messages of " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (Message message : sentMessages) {
            System.out.println(message);
            System.out.println("--------------------------------");
        }
    }

    public void sendComplaint(Employee recipient, String subject, String description) {
        if (recipient == null) {
            System.out.println("Complaint recipient does not exist.");
            return;
        }

        if (subject == null || subject.isBlank()) {
            System.out.println("Complaint subject cannot be empty.");
            return;
        }

        if (description == null || description.isBlank()) {
            System.out.println("Complaint description cannot be empty.");
            return;
        }

        if (sentComplaints == null) {
            sentComplaints = new ArrayList<>();
        }

        Complaint complaint = new Complaint(this, recipient, subject, description);

        sentComplaints.add(complaint);
        recipient.receiveComplaint(complaint);

        Database.getInstance().addLog(
                "EMPLOYEE: " + getFullName() +
                        " sent complaint to " + recipient.getFullName() +
                        ". Subject: " + subject
        );

        System.out.println("Complaint sent successfully.");
    }

    public void receiveComplaint(Complaint complaint) {
        if (receivedComplaints == null) {
            receivedComplaints = new ArrayList<>();
        }

        receivedComplaints.add(complaint);
    }

    public void viewReceivedComplaints() {
        if (receivedComplaints == null || receivedComplaints.isEmpty()) {
            System.out.println("No received complaints for " + getFullName());
            return;
        }

        System.out.println("Received complaints of " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (Complaint complaint : receivedComplaints) {
            System.out.println(complaint);
            System.out.println("--------------------------------");
        }
    }

    public void viewSentComplaints() {
        if (sentComplaints == null || sentComplaints.isEmpty()) {
            System.out.println("No sent complaints for " + getFullName());
            return;
        }

        System.out.println("Sent complaints of " + getFullName() + ":");
        System.out.println("--------------------------------");

        for (Complaint complaint : sentComplaints) {
            System.out.println(complaint);
            System.out.println("--------------------------------");
        }
    }

    public void resolveComplaint(Complaint complaint) {
        if (complaint == null) {
            System.out.println("Complaint does not exist.");
            return;
        }

        if (receivedComplaints == null || !receivedComplaints.contains(complaint)) {
            System.out.println("This complaint was not sent to " + getFullName());
            return;
        }

        complaint.resolve();

        Database.getInstance().addLog(
                "EMPLOYEE: " + getFullName() +
                        " resolved complaint from " + complaint.getSender().getFullName() +
                        ". Subject: " + complaint.getSubject()
        );

        System.out.println("Complaint resolved successfully.");
    }

    public double getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }

    public List<Message> getInbox() {
        return inbox;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public List<Complaint> getReceivedComplaints() {
        return receivedComplaints;
    }

    public List<Complaint> getSentComplaints() {
        return sentComplaints;
    }

    @Override
    public void showMenu() {
        System.out.println("Employee menu");
    }
    
}
