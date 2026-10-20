package edureach.model;

/** A multiple-choice item, tagged with the skill domain it assesses. */
public class Question {
    private String questionId;
    private String quizId;
    private String domainId;
    private String questionText;

    public Question() {}
    public Question(String questionId, String quizId, String domainId, String questionText) {
        this.questionId = questionId; this.quizId = quizId;
        this.domainId = domainId; this.questionText = questionText;
    }

    public String getQuestionId() { return questionId; }
    public String getQuizId() { return quizId; }
    public String getDomainId() { return domainId; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
}
