package edureach.model;

/** Course unit (e.g. Basic Computer Use, Internet Safety, Financial Basics). */
public class Course {
    private String courseId;
    private String title;
    private String category;
    private String instructorId;
    private String description = "";
    private boolean published = true;

    public Course() {}
    public Course(String courseId, String title, String category, String instructorId, String description) {
        this.courseId = courseId; this.title = title; this.category = category;
        this.instructorId = instructorId; this.description = description;
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getInstructorId() { return instructorId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
