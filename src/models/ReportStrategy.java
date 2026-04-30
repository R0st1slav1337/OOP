package models;

public interface ReportStrategy {
    void generate(Database database);

    String getName();
}
