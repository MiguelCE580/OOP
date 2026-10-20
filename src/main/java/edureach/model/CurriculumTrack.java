package edureach.model;

/** K-12 tracks of the optional Curriculum module (Elem -> HS). */
public enum CurriculumTrack {
    ELEMENTARY("Elementary", "Grades 1-6"),
    JUNIOR_HIGH("Junior High School", "Grades 7-10"),
    SENIOR_HIGH("Senior High School", "Grades 11-12");

    private final String label;
    private final String grades;

    CurriculumTrack(String label, String grades) { this.label = label; this.grades = grades; }

    public String label() { return label; }
    public String grades() { return grades; }
}
