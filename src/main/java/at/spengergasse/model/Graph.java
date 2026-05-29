package at.spengergasse.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final List<List<Integer>> matrix;
    private final List<String>labels;

    public Graph(List<List<Integer>> matrix) {
//        Das ist eine if-else abfrage
        this.matrix = matrix == null ? new ArrayList<>() : new ArrayList<>(matrix);
        this.labels = generateLabels(this.matrix.size());

    }
    public List<List<Integer>> getMatrix() {
        return matrix;
    }

    public List<String> getLabels() {
        return labels;
    }
    public int size() {
        return matrix.size();
    }

    private List<String> generateLabels(int size) {
        List <String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(String.valueOf((char) ('A' + i)));
        }
        return result;
    }
}
