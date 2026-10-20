package edureach.model;

import java.util.List;
import java.util.Map;

/**
 * Gradable implementation for auto-scored multiple-choice quizzes.
 * The score is computed here (never in the UI) - Encapsulation pillar.
 */
public class QuizModule implements Gradable {

    private final Quiz quiz;
    private final List<Question> questions;
    private final Map<String, String> answers;      // questionId -> selected optionId
    private final Map<String, QuestionOption> correctOptions;  // questionId -> correct option

    public QuizModule(Quiz quiz, List<Question> questions, Map<String, String> answers,
                      Map<String, QuestionOption> correctOptions) {
        this.quiz = quiz;
        this.questions = questions;
        this.answers = answers;
        this.correctOptions = correctOptions;
    }

    public Quiz getQuiz() { return quiz; }

    @Override
    public double calculateScore() {
        int correct = 0;
        for (Question q : questions) {
            String selected = answers.get(q.getQuestionId());
            QuestionOption right = correctOptions.get(q.getQuestionId());
            if (selected != null && right != null && selected.equals(right.getOptionId())) {
                correct++;
            }
        }
        return correct;
    }

    @Override
    public double getMaxScore() { return questions.size(); }

    public boolean isCorrect(String questionId) {
        String selected = answers.get(questionId);
        QuestionOption right = correctOptions.get(questionId);
        return selected != null && right != null && selected.equals(right.getOptionId());
    }
}
