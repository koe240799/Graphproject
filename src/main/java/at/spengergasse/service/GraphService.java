
package at.spengergasse.service;

import at.spengergasse.model.Graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GraphService {
    private final GraphValidation validation = new GraphValidation();

    public Graph load(InputStream inputStream) {

        List<List<Integer>> matrix = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            int rowIndex = 0;


            while ((line = reader.readLine()) != null) {

                line = normalize(line);
                List<Integer> row = parseRow(line, rowIndex);
                matrix.add(row);
                rowIndex++;


            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Einlesen der Datei ! " + e.getMessage());
        }
        validation.validate(matrix);
        return new Graph(matrix);
    }

    private List<Integer> parseRow(String line, int rowIndex) {
        String[] parts = line.split("[,;|]+");
        List<Integer> row = new ArrayList<>();

        for (int colIndex = 0; colIndex < parts.length; colIndex++) {
            String token = parts[colIndex].trim();

            token = token.replace("[", ""). replace("]", "").replace(" ", "");

            if (token.isEmpty()) {
                throw new IllegalArgumentException("Leerer Wert in der Zeile: " + rowIndex + "Spalte: " +
                        colIndex + "!");
            }

            try {

                row.add(Integer.parseInt(token));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Ungültige Zahl: " + token +
                                "in der Zeile: " + rowIndex +
                                "Spalte: " + colIndex);
            }
        }
        return row;
    }

    private String normalize(String line) {
//        trimmen und Leerzeichen entfernen
        return line.trim().replaceAll("\\s+", "");
    }
}