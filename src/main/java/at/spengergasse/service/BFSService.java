package at.spengergasse.service;

import java.util.*;

//Durchführung BFS, Erzeugung von Schritt-Informationen für Ausgabe
public class BFSService {

//    speichert Abhängigkeiten (wer hat wen entdeckt)
//    neighbor -> Liste von Vorgängern
    private final Map<Integer, List<Integer>> dependencies = new HashMap<>();

    public BFSResult bfs(int[][] matrix, int start) {

        int n = matrix.length;
//        Distanz vom Startkonoten zu jedem Knoten
        int[] dist = new int[n];
//        Vorgänger für Pfad
        int[] prev = new int[n];
//        Markierung ob Knoten bereits besucht wurde
        boolean[] visited = new boolean[n];

//        Initialisierung, alles auf "unendlich" / unbekannt setzen
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        // ================= STEP TRACKING =================
        List<BFSStep> steps = new ArrayList<>();
        List<Integer> finishedNodes = new ArrayList<>();
        int stepCounter = 1;

//        FIFO - Verfahren bei BFS
        Queue<Integer> queue = new LinkedList<>();

//        Startknoten intitalisieren
        dist[start] = 0;
        visited[start] = true;
        queue.add(start);

//        Alte Abhängigkeiten löschen (für einen neuen Durchlauf)
        dependencies.clear();

        while (!queue.isEmpty()) {

            int size = queue.size();

//            Knoten die in einem Stritt entdeckt/verarbeiten werden
            List<Integer> discoveredThisStep = new ArrayList<>();
            List<Integer> processedThisStep = new ArrayList<>();

            for (int i = 0; i < size; i++) {

//                aktueller Knoten aus Queue
                int current = queue.poll();

//                wurde in diesem Schritt verarbeitet
                processedThisStep.add(current);

//                Prüfung aller Nachbarn
                for (int neighbor = 0; neighbor < n; neighbor++) {

//                    Kante existiert wurde aber noch nicht besucht
                    if (matrix[current][neighbor] != 0 && !visited[neighbor]) {

//                        Kante markieren als wirklich besucht
                        visited[neighbor] = true;
                        queue.add(neighbor);

//                        BFS-Distanzen berechnen
                        dist[neighbor] = dist[current] + 1;
//                        Vorgänger speichern (für Pfadrekonstruktion)
                        prev[neighbor] = current;

//                        für die Ausgabe als neu entdeckt markieren
                        discoveredThisStep.add(neighbor);

//                        Abhängigkeiten speichern
                        dependencies
                                .computeIfAbsent(neighbor, k -> new ArrayList<>());

                        if (!dependencies.get(neighbor).contains(current)) {
                            dependencies.get(neighbor).add(current);
                        }
                    }
                }
            }

//            Nur speichern wenn in diesem Step etwas passiert
            if (!discoveredThisStep.isEmpty() || !processedThisStep.isEmpty()) {

                steps.add(new BFSStep(
                        stepCounter++, //Schrittnummer
                        discoveredThisStep, //neu entdeckte Knoten
                        processedThisStep, // verarbeitete Knoten
                        buildDependencyString(processedThisStep) //UI-Text
                ));
            }
        }

//        Ergebnis zusammenbauen

        BFSResult result = new BFSResult(dist, prev);
        result.setSteps(steps);
        return result;
    }

//    Baut eine Stringdarstellung der dependencies
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
