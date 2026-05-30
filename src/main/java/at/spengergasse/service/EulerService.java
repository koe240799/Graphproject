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
            cycle.add("Knoten " + node);
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
}
