package at.spengergasse.service;

import java.util.Arrays;

public class DijkstraService {
//    Berechnung des kürzesten Weges von einem Startknoten zu allen anderen Knoten im Graph

    public DijkstraResult dijkstra(int[][] graph, int start) {

//        Gesamtanzahl der Knoten im Graph
        int n = graph.length;

        int[] dist = new int[n]; // kürzeste bekannte Distanz vom Start
        int[] prev = new int[n]; // Vorgänger im kürzesten Weg
        boolean[] visited = new boolean[n]; // ob Knoten schon fixiert ist

//        Alle Distanzen auf unendlich setzen
        Arrays.fill(dist, Integer.MAX_VALUE);
//        Vorgänger noch nicht bekannt
        Arrays.fill(prev, -1);

//        Prüfung ob Startknoten gültig ist
        if (start < 0 || start >= n) {
            throw new IllegalArgumentException("Invalid start node");
        }

//        Distanz vom Start zu sich selbst
        dist[start] = 0;

//        Hauptteil (läuft n-mal durch solange knoten vorhanden sind)
        for (int i = 0; i < n; i++) {

//            Knoten mit kleinster Distanz wählen, wenn keine kleineren gefunden werden fertig
            int u = minDistance(dist, visited);
            if (u == -1) break;

//            Knoten wird als besucht markiert
            visited[u] = true;

//            Überprüfung der Nachbarknoten vom bereits besuchten Knoten
            for (int v = 0; v < n; v++) {

//                v wurde noch nicht besucht
                if (!visited[v]
//                        es gibt eine Kante u -> v
                        && graph[u][v] > 0
//                        u ist erreichbar
                        && dist[u] != Integer.MAX_VALUE
//                        neuer Weg ist kürzer als alter Weg
                        && dist[u] + graph[u][v] < dist[v]) {

//                    kürzeren Weg speichern
                    dist[v] = dist[u] + graph[u][v];
//                    Vorgänger speicher für rekonstruktion
                    prev[v] = u;
                }
            }
        }

        return new DijkstraResult(dist, prev, start);
    }

//    Methode für kleinste Distanz
    private int minDistance(int[] dist, boolean[] visited) {

        int min = Integer.MAX_VALUE;
        int index = -1;

//        nur besuchte Knoten berücksichtigen
        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                index = i;
            }
        }

//        Knoten mit kleinster distanz zurückgeben
        return index;
    }
}