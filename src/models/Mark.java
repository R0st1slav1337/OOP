package models;

public class Mark {
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    public Mark(double firstAttestation, double secondAttestation, double finalExam) {
        if (firstAttestation < 0 || firstAttestation > 30) {
            throw new IllegalArgumentException("First attestation must be between 0 and 30.");
        }

        if (secondAttestation < 0 || secondAttestation > 30) {
            throw new IllegalArgumentException("Second attestation must be between 0 and 30.");
        }

        if (finalExam < 0 || finalExam > 40) {
            throw new IllegalArgumentException("Final exam must be between 0 and 40.");
        }
        
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
    }

    public double getFirstAttestation() {
        return firstAttestation;
    }

    public double getSecondAttestation() {
        return secondAttestation;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public double getTotal() {
        return firstAttestation + secondAttestation + finalExam;
    }

    public boolean isPassed() {
        return getTotal() >= 50;
    }

    public String getLetterGrade() {
        double total = getTotal();

        if (total >= 95) return "A";
        if (total >= 90) return "A-";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "B-";
        if (total >= 70) return "C+";
        if (total >= 65) return "C";
        if (total >= 60) return "C-";
        if (total >= 55) return "D+";
        if (total >= 50) return "D";

        return "F";
    }

    public double getGpaPoints() {
        String grade = getLetterGrade();

        switch (grade) {
            case "A": return 4.0;
            case "A-": return 3.67;
            case "B+": return 3.33;
            case "B": return 3.0;
            case "B-": return 2.67;
            case "C+": return 2.33;
            case "C": return 2.0;
            case "C-": return 1.67;
            case "D+": return 1.33;
            case "D": return 1.0;
            default: return 0.0;
        }
    }

    @Override
    public String toString() {
        return "1st attestation: " + firstAttestation +
                ", 2nd attestation: " + secondAttestation +
                ", final: " + finalExam +
                ", total: " + getTotal() +
                ", grade: " + getLetterGrade() +
                ", GPA points: " + getGpaPoints();
    }
}