package at.spengergasse.util;

public class GraphDataMapper {

    public static String buildNodes(int size) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append("{")
                    .append("\"id\":").append(i).append(",")
                    .append("\"label\":\"").append(getNodeLabel(i)).append("\"")
                    .append("}");

            if (i < size - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String buildEdges(int[][] matrix) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[i].length; j++) {
                if (matrix[i][j] > 0) {
                    if (!first) sb.append(",");
                    first = false;

                    sb.append("{")
                            .append("\"from\":").append(i).append(",")
                            .append("\"to\":").append(j).append(",")
                            .append("\"label\":\"").append(matrix[i][j]).append("\"")
                            .append("}");
                }
            }
        }

        sb.append("]");
        return sb.toString();
    }

    private static String getNodeLabel(int i) {
        StringBuilder sb = new StringBuilder();

        while (i >= 0) {
            sb.insert(0, (char) ('A' + i % 26));
            i = i / 26 - 1;
        }

        return sb.toString();
    }
}