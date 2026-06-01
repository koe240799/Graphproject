package at.spengergasse.service;

import java.util.ArrayList;
import java.util.List;

//Ergebnis für Dijksta Algoritmus
// hier wird nur das Ergebnis gespeichert nicht die Berechnung
public class DijkstraResult {

//    kürzeste Distanzen vom Startknoten zu allen Knoten
    private final int[] dist;
//    Vorgänger-Knoten für jeden Knoten (zur Pfadrekonstruktion)
    private final int[] prev;
//    Startknoten
    private final int start;

//    Ergebnis wird gespeichert
    public DijkstraResult(int[] dist, int[] prev, int start) {
        this.dist = dist;
        this.prev = prev;
        this.start = start;
    }

    public int[] getDistances() {
        return dist;
    }

    public int[] getPrevious() {
        return prev;
    }

    public int getStart() {
        return start;
    }

//    Rekonstruktion des Pfades zum Zielknoten
    public List<Integer> getPathTo(int target) {

//        Liste für den Pfad
        List<Integer> path = new ArrayList<>();

//        Pfad wird von hinten nach vorne aufgebaut Ziel -> Vorgänger -> Vorgänger -> Start
        for (int at = target; at != -1; at = prev[at]) {
            path.add(0, at);
        }

        return path;
    }
}