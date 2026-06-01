package at.spengergasse.views.upload;

import at.spengergasse.model.Graph;
import at.spengergasse.service.GraphService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Upload")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.UPLOAD_SOLID)
public class UploadView extends VerticalLayout {
//    Grid um Matrix anzuzeigen / jede Zeile ist eine Liste von Strings
    private final Grid<List<String>> grid = new Grid<>();

//    Anwendung der Serviceklasse zum Einlesen und Verarbeiten der Datei
    private final GraphService service = new GraphService();

    public UploadView() {

            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);

            add(new H1("CSV Upload"));

            Div container = new Div();
            setAlignItems(Alignment.CENTER);
            container.setWidth("80%");
            container.setHeight("80%");
            container.add(grid);

//            FB speichert die hochgeladene Datei, Upload wählt die Datei aus
            FileBuffer buffer = new FileBuffer();
            Upload upload = new Upload(buffer);

//            es werden nur csv Datein angenommen
            upload.setAcceptedFileTypes(".csv");

//            EVENT: wird nur ausgelöst, wenn der Upload erfolgreich war
            upload.addSucceededListener(event -> {
                try {
                    InputStream inputStream = buffer.getInputStream();
//                    Einlesen der Datei
                    Graph graph = service.load(inputStream);
//                    Anzeige des Graphen
                    showGraph(graph);

//                    Graph wird in der Session gespeichert auf Serverseite
//                    ist mehrfach abrufbar für andere Views
                    VaadinSession.getCurrent().setAttribute("graph", graph);


                    Notification.show("Graph wurde erfolgreich geladen");

                } catch (Exception e) {
                    Notification.show(e.getMessage());
                }
            });

//            Wenn Datei entfernt wird, wird der Grid wieder zurückgesetzt
            upload.addFileRemovedListener(e -> {
                resetView();
                Notification.show("Ansicht neu geladen!");
            });

            add(upload, container);
        }

//        Methode setzt die Anzeige wieder zurück (Ergebnis leere Liste)
        private void resetView() {
            grid.removeAllColumns();
            grid.setItems(List.of());
        }


//        Methode zeigt Graphen im Grid als Tabelle an
        private void showGraph(Graph graph) {

//            Alle Spalten werden entfernt
            grid.removeAllColumns();

//            Matrix und Labels aus Graph (model) holen
            List<List<Integer>> matrix = graph.getMatrix();
            List<String> labels = graph.getLabels();

//            Wenn Matrix leer ist, keine Anzeige
            if (matrix.isEmpty()) {
                grid.setItems(List.of());
                return;
            }

            int n = matrix.size();
//            Tabellenaufbau
            grid.addColumn(row -> row.get(0))
                    .setHeader("#") //Erste Spalte #
                    .setWidth("30px")
                    .setFlexGrow(0);

//            Schleife: jede Spalte zeig den Wert aus der Zeile
            for (int i = 0; i < n; i++) {
                final int col = i;
                grid.addColumn(row -> row.get(col + 1))
                        .setHeader(labels.get(i));
            }

//            Vorbereitung der Daten für das Grid (Ausgabe)
            List<List<String>> rows = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                List<String> row = new ArrayList<>();
//                Zuweisung der Spaltennamen je Element (A....., B.....)
                row.add(labels.get(i));
                for (int j = 0; j < n; j++) {
                    row.add(String.valueOf(matrix.get(i).get(j))); //Werte aus der Zeile
                }
                rows.add(row);
            }

//            Daten ins grid setzen, Ausgabe automatisch
            grid.setItems(rows);

        }
    }
