package at.spengergasse.util;

public class GraphDataMapper {

//    Knoten als JSON String- bauen
    public static String buildNodes(int size) {

//        JSON Array starten
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append("{")
//                    Übergabe einer ID für jeden Knoten
                    .append("\"id\":").append(i).append(",")
//                    Label übergabe für Anzeige
                    .append("\"label\":\"").append(getNodeLabel(i)).append("\"")
                    .append("}");

//            zwischen den objekten wird ein , gesetzt
            if (i < size - 1) sb.append(",");
        }
        sb.append("]"); // JSON Array beenden
//        Rückgabe JSON String
        return sb.toString();
    }

//    Kanten als JSON String bauen
    public static String buildEdges(int[][] matrix) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[i].length; j++) {
//                nur echte Kanten nehmen (Gewicht > 0)
                if (matrix[i][j] > 0) {
//                    , zwischen JSON Objekten
                    if (!first) sb.append(",");
                    first = false;

                    sb.append("{")
//                            eindeutige Kanten ID
                            .append("\"id\":\"").append(i).append("-").append(j).append("\",")
//                            Startknoten
                            .append("\"from\":").append(i).append(",")
//                            Endknoten
                            .append("\"to\":").append(j).append(",")
//                            Gewicht der Kante (als label)
                            .append("\"label\":\"").append(matrix[i][j]).append("\"")
                            .append("}");
                }
            }
        }

        sb.append("]"); // JSON Array beenden
        return sb.toString();
    }


//    Knotenlabel erzeuge umwandlung von Zahl in buchstaben
    private static String getNodeLabel(int i) {
        StringBuilder sb = new StringBuilder();

        while (i >= 0) {
            sb.insert(0, (char) ('A' + i % 26));
//            Wenn Z erreicht ist werden keine zeichen sondern andere Buchstaben verwendet
//            AA, BB , a, b usw.
            i = i / 26 - 1;
        }

        return sb.toString();
    }
}