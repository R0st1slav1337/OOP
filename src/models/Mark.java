package models;

public class Mark {
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    public Mark(double firstAttestation, double secondAttestation, double finalExam) {
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
    }

    public double getTotal() {
        return firstAttestation + secondAttestation + finalExam;
    }

    @Override
    public String toString() {
        return "Total mark: " + getTotal();
    }
}