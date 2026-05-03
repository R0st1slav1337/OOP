package models;

import enums.RequestSignerRole;
import enums.RequestStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmployeeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Employee sender;
    private String title;
    private String description;

    private Employee signedBy;
    private RequestSignerRole signerRole;

    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime signedAt;
    private LocalDateTime processedAt;

    public EmployeeRequest(Employee sender, String title, String description) {
        this.sender = sender;
        this.title = title;
        this.description = description;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void sign(Employee signer) {
        if (signer == null) {
            System.out.println("Signer does not exist.");
            return;
        }

        if (!signer.canSignRequests()) {
            System.out.println("This employee cannot sign requests.");
            return;
        }

        if (status != RequestStatus.PENDING) {
            System.out.println("Only pending requests can be signed.");
            return;
        }

        this.signedBy = signer;
        this.signerRole = signer.getSigningRole();
        this.status = RequestStatus.SIGNED;
        this.signedAt = LocalDateTime.now();
    }

    public void approve() {
        if (status != RequestStatus.SIGNED) {
            System.out.println("Only signed requests can be approved.");
            return;
        }

        this.status = RequestStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }

    public void reject() {
        if (status != RequestStatus.SIGNED) {
            System.out.println("Only signed requests can be rejected.");
            return;
        }

        this.status = RequestStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    public Employee getSender() {
        return sender;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Employee getSignedBy() {
        return signedBy;
    }

    public RequestSignerRole getSignerRole() {
        return signerRole;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    @Override
    public String toString() {
        return "[" + createdAt + "] Request from " + sender.getFullName() +
                "\nTitle: " + title +
                "\nDescription: " + description +
                "\nStatus: " + status +
                "\nSigned by: " + (signedBy == null ? "Not signed" : signedBy.getFullName() + " (" + signerRole + ")") +
                "\nSigned at: " + (signedAt == null ? "Not signed yet" : signedAt) +
                "\nProcessed at: " + (processedAt == null ? "Not processed yet" : processedAt);
    }
}
