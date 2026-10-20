package edureach.model;

/** One topic of the K-12 curriculum (Mathematics is the flagship subject). */
public class CurriculumTopic {
    private String topicId;
    private CurriculumTrack track;
    private String subject;          // Mathematics | Science | English
    private String title;
    private String description;
    private int estimatedMinutes;

    public CurriculumTopic() {}
    public CurriculumTopic(String topicId, CurriculumTrack track, String subject,
                           String title, String description, int estimatedMinutes) {
        this.topicId = topicId; this.track = track; this.subject = subject;
        this.title = title; this.description = description;
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getTopicId() { return topicId; }
    public CurriculumTrack getTrack() { return track; }
    public String getSubject() { return subject; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
}
