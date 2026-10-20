package edureach.model;

/** Content item inside a course, with the type of learning media. */
public class Lesson {
    private String lessonId;
    private String courseId;
    private String title;
    private String media;          // Slides | Video | PDF | Activity
    private int sequenceNo;
    private String content = "";   // short body text shown in the learner view
    /** Skill domain this lesson reinforces (used by remedial plans). */
    private String domainId = "";

    public Lesson() {}
    public Lesson(String lessonId, String courseId, String title, String media,
                  int sequenceNo, String content, String domainId) {
        this.lessonId = lessonId; this.courseId = courseId; this.title = title;
        this.media = media; this.sequenceNo = sequenceNo;
        this.content = content; this.domainId = domainId;
    }

    public String getLessonId() { return lessonId; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMedia() { return media; }
    public void setMedia(String media) { this.media = media; }
    public int getSequenceNo() { return sequenceNo; }
    public String getContent() { return content; }
    public String getDomainId() { return domainId; }
}
