package edureach.data;

import edureach.model.Course;
import edureach.model.Lesson;

import java.util.List;
import java.util.Optional;

public interface CourseDAO {
    List<Course> findAll();
    Optional<Course> findById(String courseId);
    List<Lesson> lessonsOf(String courseId);
    void save(Course course);
}
