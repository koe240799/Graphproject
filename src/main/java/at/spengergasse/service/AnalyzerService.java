package at.spengergasse.service;

import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

public class AnalyzerService {
    //    Knoten die ich nicht erreichen kann "infinite"
    public static final int INF = 999999;

    //    Berechnung der kürzesten Wege aller Knoten nach Floyd Warshall
    public int[][] floydWarshall(int[][] graph) {
        int n = graph.length;
        int[][] dist = new int[n][n];

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
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;

    }

//    Exzentrizität

    public int[] excentricity(int[][] dist) {

        int n = dist.length;
        int[] ext = new int[n];

        for (int i = 0; i < n; i++) {
            int max = 0;

            for (int j = 0; j < n; j++) {

                if (dist[i][j] != INF) {
                    max = Math.max(max, dist[i][j]);
                }
            }

            ext[i] = max;
        }

        return ext;
    }

    //    Radius / kleinste Exzentrizität
    public int radius(int[] ext) {
        int min = ext[0];

        for (int i = 0; i < ext.length; i++) {
            if (ext[i] < min) min = ext[i];
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

        int rad = radius(ext);

        List<Integer> center = new ArrayList<>();

        // 👉 über ALLE Knoten iterieren
        for (int i = 0; i < ext.length; i++) {

            if (ext[i] == rad) {
                center.add(i);
            }
        }

        return center;
    }
}