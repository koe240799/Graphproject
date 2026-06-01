
package at.spengergasse.service;

import at.spengergasse.model.Graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GraphService {
    private final GraphValidation validation = new GraphValidation();

//    Methode liest eine Datei (InputStream) ein und erstellte ein Grph-Objekt
    public Graph load(InputStream inputStream) {

//        Speicherung der Matrix (Liste von Listen)
        List<List<Integer>> matrix = new ArrayList<>();

//        BR lieste die Datei zeilenweise ein
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line; // aktuelle Zeile
            int rowIndex = 0; // aktuelle Zeilennummer (für Fehlermeldung)

//            Einlesen jeder Zeile bis keine mehr da sind!
            while ((line = reader.readLine()) != null) {

                line = normalize(line); // Bereinigung der Zeilen (" ")
                List<Integer> row = parseRow(line, rowIndex); // Zeilenumwandung in Integer
                matrix.add(row);
//                Zeilenindex erhöhen
                rowIndex++;


            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Einlesen der Datei ! " + e.getMessage());
        }
//        Validierung der Zeilen mit zugriff auf GraphValidation
        validation.validate(matrix);
        return new Graph(matrix);
    }

    // Methode wandelt einzelen Zeile (String) in eine Liste von Integern um
    private List<Integer> parseRow(String line, int rowIndex) {
//        Zerlegung der Zeile anhand der Trennzeichen
        String[] parts = line.split("[,;|]+");
        List<Integer> row = new ArrayList<>();

//        Schleife: Durchlauf aller Werte in der Zeile
        for (int colIndex = 0; colIndex < parts.length; colIndex++) {
            String token = parts[colIndex].trim();

//            Entfernung von unerwünschten Zeichen und Leerzeichen
            token = token.replace("[", ""). replace("]", "").replace(" ", "");

            if (token.isEmpty()) {
                throw new IllegalArgumentException("Leerer Wert in der Zeile: " + rowIndex + "Spalte: " +
                        colIndex + "!");
            }


//            Versuch String in eine Zahl umzuwandeln, wenn es keine gültige Zahl ist -> Fehler
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

//    Zeilenbereinigung wird in der load Methode eingesetzt
    private String normalize(String line) {
//        trimmen und Leerzeichen entfernen
        return line.trim().replaceAll("\\s+", "");
    }
}