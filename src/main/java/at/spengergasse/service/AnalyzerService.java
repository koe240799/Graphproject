package at.spengergasse.service;

import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

public class AnalyzerService {
    //    Knoten die ich nicht erreichen kann "infinite"
    public static final int INF = 999999;

    //    Berechnung der kürzesten Wege zwischen Knotenpaaren nach Floyd Warshall
    public int[][] floydWarshall(int[][] graph) {

        int n = graph.length; //Knotenanzahl
        int[][] dist = new int[n][n]; //Distanzmatrix

//        Initialisierung Distanzmatrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (i == j) {
//                    Entferung vom Knoten zu sich selbst!
                    dist[i][j] = 0;
                } else if (graph[i][j] == 0) {
//                    Wenn keine Kante vorhanden ist (infinite)
                    dist[i][j] = INF;
                } else {
//                    Wenn es eine Kante gibt
                    dist[i][j] = graph[i][j];
                }
            }
        }
//        Hier werden 3 Schleifen durchlaufen
        for (int k = 0; k < n; k++) { //Zwischenknoten
            for (int i = 0; i < n; i++) { //Startknoten
                for (int j = 0; j < n; j++) { // Zielknoten

//                    Ziel: ist der kürzeste weg i -> j , oder ist er kürzer wenn ich über
//                            den Zwischenknoten k gehe i -> k -> j

                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
//                        Überschreibung des alten Weges
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;

    }

//    Exzentrizität -> größte Distanz zu einem anderen erreichbaren Knoten

    public int[] excentricity(int[][] dist) {

        int n = dist.length; // Anzahl der Knoten im Graphen
        int[] ext = new int[n]; // Ergebnis Array

        for (int i = 0; i < n; i++) {
            int max = 0; // größte Distanz für Knoten i

            for (int j = 0; j < n; j++) {

//                nur erreichbare Knoten berückstichtigen
                if (dist[i][j] != INF) {
                    max = Math.max(max, dist[i][j]);
                }
            }

            ext[i] = max; // Exzentrizität speichern
        }

        return ext;
    }

    //    Radius / kleinste Exzentrizität
    public int radius(int[] ext) {
        int min = ext[0]; //Startwert

        for (int i = 0; i < ext.length; i++) {
//            Math.min = Vergleich von zwei werden, min ist der kleine bekannte Wert
//                    wird ext[i] ist der aktuelle Wert
            min = Math.min(min, ext[i]);
        }
        return min;
    }

//    Durchmesser / größte Exzentrizität
    public int diameter(int[] ext) {
        int max = ext[0];
        for (int i = 0; i < ext.length; i++) {
            if (ext[i] > max) max = ext[i];
        }
        return max;
    }

//    Zentrum / alle Knoten mit gleichem Radius

    public List<Integer> center(int[] ext) {

        // ich greife auf die Methode radius zu und hole mir das Ergebnis
        int rad = radius(ext);

//        Alle Knoten die zum Zentrum gehören werden hier gespeichert
        List<Integer> center = new ArrayList<>();

        // über ALLE Knoten iterieren
        for (int i = 0; i < ext.length; i++) {
//            Wenn die Exzentrizität der Knoten dem Radius entspricht, dann ist er "zentral"
            if (ext[i] == rad) {
                center.add(i);
            }
        }

        return center;
    }
}