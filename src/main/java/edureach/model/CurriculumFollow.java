package edureach.model;

import java.util.ArrayList;
import java.util.List;

/** A student optionally following a K-12 curriculum track. */
public class CurriculumFollow {
    private String studentId;
    private CurriculumTrack track;
    private List<String> studiedTopics = new ArrayList<>();

    public CurriculumFollow() {}
    public CurriculumFollow(String studentId, CurriculumTrack track) {
        this.studentId = studentId; this.track = track;
    }

    public String getStudentId() { return studentId; }
    public CurriculumTrack getTrack() { return track; }
    public List<String> getStudiedTopics() { return studiedTopics; }
}
