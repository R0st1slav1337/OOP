package models;

public class CoursePerformanceReport implements ReportStrategy {
    private Course course;

    public CoursePerformanceReport(Course course) {
        this.course = course;
    }

    @Override
    public void generate(Database database) {
        int markedStudents = 0;
        int passedStudents = 0;
        int failedStudents = 0;
        double totalScore = 0;

        for (Student student : course.getStudents()) {
            Mark mark = student.getMarks().get(course);

            if (mark != null) {
                markedStudents++;
                totalScore += mark.getTotal();

                if (mark.isPassed()) {
                    passedStudents++;
                } else {
                    failedStudents++;
                }
            }
        }

        System.out.println("=== Course Performance Report ===");
        System.out.println("Course: " + course.getName());

        if (markedStudents == 0) {
            System.out.println("No marks found for this course.");
            return;
        }

        double averageScore = totalScore / markedStudents;

        System.out.println("Marked students: " + markedStudents);
        System.out.printf("Average score: %.2f%n", averageScore);
        System.out.println("Passed students: " + passedStudents);
        System.out.println("Failed students: " + failedStudents);
    }

    @Override
    public String getName() {
        return "Course Performance Report for " + course.getName();
    }
}