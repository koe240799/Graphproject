package at.spengergasse.service;

import java.util.ArrayList;
import java.util.List;

public class BFSResult {

//    kürzester Abstand vom Startknoten zu jedem anderen Knoten
    private final int[] distances;
//    Vorgängerarray für Pfadrekonstruktion
    private final int[] previous;
//    Schrittweise darstellung des BFS-Ablaufs
    private List<BFSStep> steps;

//    Befüllung erfolgt über BFS-Service
    public BFSResult(int[] distances, int[] previous) {
        this.distances = distances;
        this.previous = previous;
    }

//    Rückgabe kürzeste Distanzen
    public int[] getDistances() {
        return distances;
    } //

//    Rückgabe vorgänger Knoten
    public int[] getPrevious() {
        return previous;
    }

//    gibt die gespeicherten BFS-Schritte zurück
    public List<BFSStep> getSteps() {
        return steps;
    }

//    setzt BFS-Schritte nachträglich (Service erzeugt Stept während Algorithmus läuft)
    public void setSteps(List<BFSStep> steps) {
        this.steps = steps;
    }
}