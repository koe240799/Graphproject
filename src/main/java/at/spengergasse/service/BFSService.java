package at.spengergasse.service;

import java.util.*;

public class BFSService {
    private final Map<Integer, List<Integer>> dependencies = new HashMap<>();

    public BFSResult bfs(int[][] matrix, int start) {

        int n = matrix.length;
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        // 🔥 NEU: Schritt-Tracking
        List<BFSStep> steps = new ArrayList<>();
        List<Integer> finishedNodes = new ArrayList<>();
        int stepCounter = 1;

        Queue<Integer> queue = new LinkedList<>();
        dist[start] = 0;
        visited[start] = true;
        queue.add(start);

        dependencies.clear();

        while (!queue.isEmpty()) {

            int size = queue.size();

            List<Integer> discoveredThisStep = new ArrayList<>();
            List<Integer> processedThisStep = new ArrayList<>();

            for (int i = 0; i < size; i++) {

                int current = queue.poll();

                processedThisStep.add(current); // 🔵 wirklich verarbeitet

                for (int neighbor = 0; neighbor < n; neighbor++) {

                    if (matrix[current][neighbor] != 0 && !visited[neighbor]) {

                        visited[neighbor] = true;
                        queue.add(neighbor);

                        dist[neighbor] = dist[current] + 1;
                        prev[neighbor] = current;

                        discoveredThisStep.add(neighbor); // 🟡 neu entdeckt

                        dependencies
                                .computeIfAbsent(neighbor, k -> new ArrayList<>());

                        if (!dependencies.get(neighbor).contains(current)) {
                            dependencies.get(neighbor).add(current);
                        }
                    }
                }
            }

            if (!discoveredThisStep.isEmpty() || !processedThisStep.isEmpty()) {

                steps.add(new BFSStep(
                        stepCounter++,
                        discoveredThisStep,
                        processedThisStep,
                        buildDependencyString(processedThisStep)
                ));
            }
        }

        BFSResult result = new BFSResult(dist, prev);
        result.setSteps(steps);
        return result;
    }


    private String buildDependencyString(List<Integer> nodes) {

        Set<String> result = new LinkedHashSet<>();

        for (int node : nodes) {

            List<Integer> deps = dependencies.get(node);

            if (deps != null) {
                for (Integer d : deps) {
                    result.add(String.valueOf((char) ('A' + d)));
                }
            }
        }

        return String.join(" , ", result);
    }

}
