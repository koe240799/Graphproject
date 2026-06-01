package at.spengergasse.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
//    Adjazenzmatrix: spreichert die Verbindungen zwischen Knoten
    private final List<List<Integer>> matrix;
//    Knoten Label: zahlen aus der Matrix werden in Buchstaben umgewandelt (A, B, C)
    private final List<String>labels;

    public Graph(List<List<Integer>> matrix) {
//    Wenn übergeben Matrix null ist wird ein leeres Array übergeben, sonst wird
//    wird eine neue Liste mit den enthaltenen Elementen übergeben
        this.matrix = matrix == null ? new ArrayList<>() : new ArrayList<>(matrix);
//        automatische Label Vergabe
        this.labels = generateLabels(this.matrix.size());

    }
    public List<List<Integer>> getMatrix() {
        return matrix;
    }

    public List<String> getLabels() {
        return labels;
    }
//    Rückgabe Knotenanzahl
    public int size() {
        return matrix.size();
    }

//    Hilfsmethode zur Konotengenerierung
    private List<String> generateLabels(int size) {
//        Liste für Labels wird erstellt
        List <String> result = new ArrayList<>();
//        Schleife: läuft so oft durch bis alle Knoten abgearbeitet sind
        for (int i = 0; i < size; i++) {
//         'A' ist eine char -> wird als eine Zahl gespeichter
//         'A'+ 1 Je höher die Zahl, desto höher der Buchstabe
//          und Umwandlung in in String
            result.add(String.valueOf((char) ('A' + i)));
        }
        return result;
    }


//    Hier wird die eingelesene Matrix in ein int Konvertiert
    public int [][] toMatrixArray(){
//        Knotenanzahl
        int n = matrix.size();
//        Neues 2D Array mit n Zeilen und n Spalten
        int[][] result = new int[n][n];

        for (int i = 0; i < n; i++){ //Zeile
            for (int j = 0; j < n; j++){ // Spalte
//                Werte werden aus der Liste geholt und in matrix.get(i).get(j) gespeichert
//                es entsteht eine neue Matrix
                result[i][j] = matrix.get(i).get(j);
            }
        }
        return result;
    }
}
