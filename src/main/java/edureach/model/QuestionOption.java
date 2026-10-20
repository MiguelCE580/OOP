package edureach.model;

/** One answer choice of a question. */
public class QuestionOption {
    private String optionId;
    private String questionId;
    private String optionText;
    private boolean correct;

    public QuestionOption() {}
    public QuestionOption(String optionId, String questionId, String optionText, boolean correct) {
        this.optionId = optionId; this.questionId = questionId;
        this.optionText = optionText; this.correct = correct;
    }

    public String getOptionId() { return optionId; }
    public String getQuestionId() { return questionId; }
    public String getOptionText() { return optionText; }
    public boolean isCorrect() { return correct; }
}
