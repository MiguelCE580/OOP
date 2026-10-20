package edureach.model;

/** Per-item answer of a submission (feeds the quiz analytics). */
public class SubmissionAnswer {
    private String subId;
    private String questionId;
    private String selectedOptionId;   // empty when unanswered
    private boolean correct;

    public SubmissionAnswer() {}
    public SubmissionAnswer(String subId, String questionId, String selectedOptionId, boolean correct) {
        this.subId = subId; this.questionId = questionId;
        this.selectedOptionId = selectedOptionId; this.correct = correct;
    }

    public String getSubId() { return subId; }
    public String getQuestionId() { return questionId; }
    public String getSelectedOptionId() { return selectedOptionId; }
    public boolean isCorrect() { return correct; }
}
