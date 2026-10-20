package edureach.data.file;

import edureach.data.CourseDAO;
import edureach.data.JsonStore;
import edureach.model.Course;
import edureach.model.Lesson;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Single-file implementation of {@link CourseDAO}. */
public class FileCourseDAO implements CourseDAO {

    @Override
    public List<Course> findAll() {
        return JsonStore.get().db().courses;
    }

    @Override
    public Optional<Course> findById(String courseId) {
        return findAll().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
    }

    @Override
    public List<Lesson> lessonsOf(String courseId) {
        return JsonStore.get().db().lessons.stream()
                .filter(l -> l.getCourseId().equals(courseId))
                .sorted(Comparator.comparingInt(Lesson::getSequenceNo))
                .toList();
    }

    @Override
    public void save(Course course) {
        List<Course> all = findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getCourseId().equals(course.getCourseId())) {
                all.set(i, course);
                JsonStore.get().save();
                return;
            }
        }
        all.add(course);
        JsonStore.get().save();
    }
}
