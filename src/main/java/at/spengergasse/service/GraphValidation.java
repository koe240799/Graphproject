package at.spengergasse.service;

import java.util.List;

public class GraphValidation {
    public void validate(List<List<Integer>> matrix) {
        if (matrix == null || matrix.isEmpty()){
            throw new IllegalArgumentException("Matrix ist leer!");

        }

        int size = matrix.size();

        for(List<Integer> row : matrix){
            if(row.size() != size){
                throw new IllegalArgumentException("Matrix ist nicht quadratisch!");
            }
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
