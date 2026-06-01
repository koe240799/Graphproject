package at.spengergasse.service;

import java.util.List;

//BFSStep beschreibt jeden einzelnen Schritt der BFS-Ausführung für Visualisierung
//Jeder Step entspricht einer BFS_Ebene (Level-By_Level)
public class BFSStep {

//    laufende Nummer 1, 2 3
    private final int step;
//    Knoten die in diesem Schritt entdeckt wurden
    private final List<Integer> discovered;
//    Knoten, die in diesem Schritt verarbeitet wurden
    private final List<Integer> processed;
//    String-Darstellung von Abhängigkeiten
    private final String dependencies;

//    Wird wieder von BFSService befüllt
    public BFSStep(int step,
                   List<Integer> discovered,
                   List<Integer> processed,
                   String dependencies) {

        this.step = step;
        this.discovered = discovered;
        this.processed = processed;
        this.dependencies = dependencies;
    }

    public int getStep() {
        return step;
    }

    public List<Integer> getDiscovered() {
        return discovered;
    }

    public List<Integer> getProcessed() {
        return processed;
    }

    public String getDependencies() {
        return dependencies;
    }
}