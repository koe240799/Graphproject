package at.spengergasse.model;

//Einfache Beschreibung des Euler-Koonzepts für Ausgabe
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
