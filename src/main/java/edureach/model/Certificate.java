package edureach.model;

/** Issued credential with a unique, checkable verification code (proposal P4). */
public class Certificate {
    private String certId;
    private String studentId;
    private String courseId;
    private String issuedOn;
    private String verificationCode;   // UNIQUE, e.g. ER-2026-7A4Q-0100
    private double finalScorePct;

    public Certificate() {}
    public Certificate(String certId, String studentId, String courseId,
                       String issuedOn, String verificationCode, double finalScorePct) {
        this.certId = certId; this.studentId = studentId; this.courseId = courseId;
        this.issuedOn = issuedOn; this.verificationCode = verificationCode;
        this.finalScorePct = finalScorePct;
    }

    public String getCertId() { return certId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getIssuedOn() { return issuedOn; }
    public String getVerificationCode() { return verificationCode; }
    public double getFinalScorePct() { return finalScorePct; }
}
