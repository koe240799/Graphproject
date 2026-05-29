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
    private final Grid<List<String>> grid = new Grid<>();
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


            FileBuffer buffer = new FileBuffer();
            Upload upload = new Upload(buffer);

            upload.setAcceptedFileTypes(".csv");


            upload.addSucceededListener(event -> {
                try {
                    InputStream inputStream = buffer.getInputStream();

                    Graph graph = service.load(inputStream);
                    showGraph(graph);

//                    Speicherung des Graphen in HTTP-Session des Browsers
                    VaadinSession.getCurrent().setAttribute("graph", graph);


                    Notification.show("Graph wurde erfolgreich geladen");

                } catch (Exception e) {
                    Notification.show(e.getMessage());
                }
            });

            upload.addFileRemovedListener(e -> {
                resetView();
                Notification.show("Ansicht neu geladen!");
            });

            add(upload, container);
        }

        private void resetView() {
            grid.removeAllColumns();
            grid.setItems(List.of());
        }


        // 🔥 BLOCKER + VIEW LOGIC
        private void showGraph(Graph graph) {

            grid.removeAllColumns();

            List<List<Integer>> matrix = graph.getMatrix();
            List<String> labels = graph.getLabels();

            if (matrix.isEmpty()) {
                grid.setItems(List.of());
                return;
            }

            int n = matrix.size();

            grid.addColumn(row -> row.get(0))
                    .setHeader("#")
                    .setWidth("30px")
                    .setFlexGrow(0);

            for (int i = 0; i < n; i++) {
                final int col = i;
                grid.addColumn(row -> row.get(col + 1))
                        .setHeader(labels.get(i));
            }

            List<List<String>> rows = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                List<String> row = new ArrayList<>();
                row.add(labels.get(i));
                for (int j = 0; j < n; j++) {
                    row.add(String.valueOf(matrix.get(i).get(j)));
                }
                rows.add(row);
            }
            grid.setItems(rows);

        }
    }
