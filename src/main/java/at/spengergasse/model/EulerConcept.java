package at.spengergasse.model;

public class EulerConcept {
    private final String title;
    private final String description;

    public EulerConcept(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
