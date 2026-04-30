package models;

public class OverallPerformanceReport implements ReportStrategy {
    @Override
    public void generate(Database database) {
        int studentCount = 0;
        double totalGpa = 0;
        Student topStudent = null;
        double highestGpa = -1;

        for (User user : database.getUsers()) {
            if (user instanceof Student) {
                Student student = (Student) user;

                double gpa = student.calculateGpa();

                studentCount++;
                totalGpa += gpa;

                if (gpa > highestGpa) {
                    highestGpa = gpa;
                    topStudent = student;
                }
            }
        }

        System.out.println("=== Overall Academic Performance Report ===");

        if (studentCount == 0) {
            System.out.println("No students found.");
            return;
        }

        double averageGpa = totalGpa / studentCount;

        System.out.println("Number of students: " + studentCount);
        System.out.printf("Average GPA: %.2f%n", averageGpa);

        if (topStudent != null) {
            System.out.println("Top student: " + topStudent.getFullName());
            System.out.printf("Top GPA: %.2f%n", highestGpa);
        }
    }

    @Override
    public String getName() {
        return "Overall Academic Performance Report";
    }
}