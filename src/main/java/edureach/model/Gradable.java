package edureach.model;

/**
 * Polymorphism pillar (proposal 5.2): every assessment type implements
 * this so the Progress Tracker can handle them uniformly.
 */
public interface Gradable {
    /** Points obtained by the learner. */
    double calculateScore();
    /** Maximum obtainable points. */
    double getMaxScore();

    default double scorePercent() {
        double max = getMaxScore();
        return max <= 0 ? 0 : Math.round(calculateScore() / max * 10000.0) / 100.0;
    }
}
