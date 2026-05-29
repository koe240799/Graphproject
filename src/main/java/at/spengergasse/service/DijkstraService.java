package at.spengergasse.service;

import java.util.Arrays;

public class DijkstraService {
    public int[] dijkstra(int[][] graph, int start) {
        int n = graph.length;

        int[] dist = new int[n]; //kürzeste Distanz
        boolean[] visited = new boolean[n];

        // Alle Distanzen werden auf maximal gestellt
        Arrays.fill(dist, Integer.MAX_VALUE);

        dist[start] = 0;

        for (int i = 0; i < n; i++) {
            int u = minDistance(dist, visited);

            if (u == -1) break;

            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v]
                    &&graph[u][v] != 0
                        && dist[u] != Integer.MAX_VALUE
                        && dist[u] + graph[u][v] < dist[v]){
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        return dist;
    }

    private int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < visited.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                index = i;
            }

        }
        return index;
    }
}
