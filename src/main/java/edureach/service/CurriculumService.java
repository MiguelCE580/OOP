package edureach.service;

import edureach.data.JsonStore;
import edureach.model.CurriculumFollow;
import edureach.model.CurriculumTopic;
import edureach.model.CurriculumTrack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Optional K-12 curriculum module (Elem -> HS), Mathematics first.
 * Students may follow a track and mark topics as studied; nothing here
 * affects course grades - it is a supplemental learning path.
 */
public final class CurriculumService {

    private CurriculumService() {}

    public static List<CurriculumTopic> topics(CurriculumTrack track, String subject) {
        return JsonStore.get().db().curriculumTopics.stream()
                .filter(t -> t.getTrack() == track)
                .filter(t -> subject == null || t.getSubject().equals(subject))
                .sorted(Comparator.comparing(CurriculumTopic::getTopicId))
                .toList();
    }

    public static List<String> subjectsOf(CurriculumTrack track) {
        return JsonStore.get().db().curriculumTopics.stream()
                .filter(t -> t.getTrack() == track)
                .map(CurriculumTopic::getSubject)
                .distinct()
                .sorted((a, b) -> a.equals("Mathematics") ? -1 : b.equals("Mathematics") ? 1 : a.compareTo(b))
                .toList();
    }

    public static Optional<CurriculumFollow> follow(String studentId) {
        return JsonStore.get().db().curriculumFollows.stream()
                .filter(f -> f.getStudentId().equals(studentId)).findFirst();
    }

    public static void toggleFollow(String studentId, CurriculumTrack track) {
        var follows = JsonStore.get().db().curriculumFollows;
        Optional<CurriculumFollow> existing = follow(studentId);
        if (existing.isPresent() && existing.get().getTrack() == track) {
            follows.remove(existing.get());
        } else {
            existing.ifPresent(follows::remove);
            follows.add(new CurriculumFollow(studentId, track));
        }
        JsonStore.get().save();
    }

    public static void toggleStudied(String studentId, CurriculumTrack track, String topicId) {
        CurriculumFollow f = follow(studentId).orElseGet(() -> {
            CurriculumFollow nf = new CurriculumFollow(studentId, track);
            JsonStore.get().db().curriculumFollows.add(nf);
            return nf;
        });
        if (!f.getStudiedTopics().remove(topicId)) f.getStudiedTopics().add(topicId);
        JsonStore.get().save();
    }

    public static int progressPct(String studentId, CurriculumTrack track) {
        long total = topics(track, null).size();
        if (total == 0) return 0;
        long studied = follow(studentId)
                .map(f -> f.getStudiedTopics().stream()
                        .filter(id -> topics(track, null).stream()
                                .anyMatch(t -> t.getTopicId().equals(id)))
                        .count())
                .orElse(0L);
        return (int) Math.round(studied * 100.0 / total);
    }
}
