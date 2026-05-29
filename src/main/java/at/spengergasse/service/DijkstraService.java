package at.spengergasse.service;

import java.util.Arrays;

public class DijkstraService {

    public DijkstraResult dijkstra(int[][] graph, int start) {

        int n = graph.length;

        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        if (start < 0 || start >= n) {
            throw new IllegalArgumentException("Invalid start node");
        }

        dist[start] = 0;

        for (int i = 0; i < n; i++) {

            int u = minDistance(dist, visited);
            if (u == -1) break;

            visited[u] = true;

            for (int v = 0; v < n; v++) {

                if (!visited[v]
                        && graph[u][v] > 0
                        && dist[u] != Integer.MAX_VALUE
                        && dist[u] + graph[u][v] < dist[v]) {

                    dist[v] = dist[u] + graph[u][v];
                    prev[v] = u;
                }
            }
        }

        return new DijkstraResult(dist, prev, start);
    }

    private int minDistance(int[] dist, boolean[] visited) {

        int min = Integer.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                index = i;
            }
        }

        return index;
    }
}