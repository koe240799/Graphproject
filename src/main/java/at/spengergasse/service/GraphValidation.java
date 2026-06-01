package at.spengergasse.service;

import java.util.List;

public class GraphValidation {

//    Prüfung ob übergebene Matix gültig ist
    public void validate(List<List<Integer>> matrix) {
//        Leere Matrix wirft eine Fehlermeldung aus
        if (matrix == null || matrix.isEmpty()){
            throw new IllegalArgumentException("Matrix ist leer!");

        }

//        Zeilenanzahl = Spaltenanzahl
        int size = matrix.size();

//        Prüfung ob aktuelle Zeile gleiche länge wie die Matrixgröße hat,
//        notwendig für quadratische Matrix
        for(List<Integer> row : matrix){
            if(row.size() != size){
                throw new IllegalArgumentException("Matrix ist nicht quadratisch!");
            }
//          Scheifendurchlauf um zu eruieren ob null und negativ Werte in der Matrix sind
//          um fehlern bei der Bereinigung zuvorzukommen
            for(Integer val : row){
                if(val == null){
                    throw new IllegalArgumentException("Null Werte nicht erlaubt!");
                }
                if(val < 0){
                    throw new IllegalArgumentException("Negative Werte nicht erlaubt!");
                }
            }
        }
    }
}
