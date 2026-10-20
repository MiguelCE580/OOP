package edureach.model;

/** Skill area assessed by quiz items (competency-oriented assessment). */
public class SkillDomain {
    private String domainId;
    private String name;
    /** Badge name awarded when mastery >= 80%. */
    private String badgeName;

    public SkillDomain() {}
    public SkillDomain(String domainId, String name, String badgeName) {
        this.domainId = domainId; this.name = name; this.badgeName = badgeName;
    }

    public String getDomainId() { return domainId; }
    public String getName() { return name; }
    public String getBadgeName() { return badgeName; }
    public void setName(String name) { this.name = name; }
}
