package at.spengergasse.util;

import java.util.ArrayList;
import java.util.List;

public class GraphVisualAdapter {

    // ✅ NUR EINE KANTE PRO VERBINDUNG (kein Doppelgraph mehr)
    public static List<int[]> toEdges(int[][] matrix) {

        List<int[]> edges = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) { // 🔥 FIX: i < j
                if (matrix[i][j] == 1) {
                    edges.add(new int[]{i, j});
                }
            }
        }

        return edges;
    }
}