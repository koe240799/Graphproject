package at.spengergasse.service;

import at.spengergasse.model.EulerConcept;
import at.spengergasse.util.EulerDataUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

public class EulerService {

    public List<String> getCycle(int[][] matrix) {

        List<String> cycle = new ArrayList<>();
        int n = matrix.length;

        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            copy[i] = matrix[i].clone();
        }

        Stack<Integer> stack = new Stack<>();
        List<Integer> path = new ArrayList<>();

        stack.push(0);

        while (!stack.isEmpty()) {
            int v = stack.peek();
            boolean hasEdge = false;

            for (int i = 0; i < n; i++) {
                if (copy[v][i] > 0) {
                    stack.push(i);
                    copy[v][i]--;
                    copy[i][v]--;
                    hasEdge = true;
                    break;
                }
            }

            if (!hasEdge) {
                path.add(stack.pop());
            }
        }

        Collections.reverse(path);

        for (Integer node : path) {
            cycle.add(String.valueOf(node));
        }

        return cycle;
    }

    public boolean hasEulerCycle(int[][] matrix){
        for(int i = 0; i < matrix.length; i++){
            int degree = 0;
            for(int j = 0; j < matrix[i].length; j++){
                degree += matrix[i][j];
            }
            if(degree % 2 != 0){
                return false;
            }
        }
        return true;
    }

    public String getEulerType(int[][] matrix){
        int oddcount = 0;

        for (int i = 0; i < matrix.length; i++){
            int degree = 0;
            for(int j = 0; j < matrix[i].length; j++){
                degree += matrix[i][j];
            }
            if(degree % 2 != 0){
                oddcount++;
            }
        }
        if(oddcount == 0){
            return "cycle";
        } else if (oddcount == 2) {
            return "path";
        }
        else{
            return "none";
        }
    }

    public List<String> getPath(int[][] matrix) {

        int start = findStartVertex(matrix);

        List<String> path = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(start);

        int [][] copy = new int [matrix.length][matrix.length];

        for(int i = 0; i < matrix.length; i++){
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix.length);
        }

        while(!stack.isEmpty()){
            int v = stack.peek();
            boolean hasEdge = false;

            for(int i = 0; i < matrix.length; i++){
                if(copy[v][i] > 0) {
                    stack.push(i);
                    copy[v][i]--;
                    copy[i][v]--;
                    hasEdge = true;
                    break;
                }
            }
            if(!hasEdge){
                path.add(String.valueOf(stack.pop()));
            }
        }
        Collections.reverse(path);
        return path;
    }

    public int findStartVertex(int[][] matrix) {
        int odd = 0;
        int start = 0;

        for(int i = 0; i < matrix.length; i++){
            int degree = 0;
            for(int j = 0; j < matrix[i].length; j++){
                degree += matrix[i][j];
            }
            if(degree % 2 != 0){
                odd++;
                start = i;
            }
        }
        if(odd == 2) return start;
        return 0;
    }
}
