package at.spengergasse.views.analyzer;

import at.spengergasse.model.Graph;
import at.spengergasse.service.AnalyzerService;
import at.spengergasse.service.GraphService;
import at.spengergasse.views.upload.UploadView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Analyzer")
@Route("analyzer")
@Menu(order = 1, icon = LineAwesomeIconUrl.CALCULATOR_SOLID)
public class AnalyzerView extends VerticalLayout {
    private final AnalyzerService service = new AnalyzerService();

    public AnalyzerView() {
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Graph graph = (Graph) VaadinSession.getCurrent().getAttribute("graph");

        if (graph == null) {
            H2 message = new H2(" Kein Graph vorhanden! Bitte zuerst Upload durchfüren.");


            Button uploadButton = new Button("Zum Upload", e ->
                    UI.getCurrent().navigate(UploadView.class));

            HorizontalLayout header = new HorizontalLayout(message, uploadButton);
            header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            add(header);
            return;
        }


//        Graph in eine Matrix umwandeln
        int[][] matrix = graph.toMatrixArray();

//        Berechnungen aus der AnalyzerService Klasse übernehmen
//        kürzesten Wege:
        int[][] dist = service.floydWarshall(matrix);

//        Exzentritität
        int[] ext = service.excentricity(dist);

//        Radius
        int radius = service.radius(ext);

//        Durchmesser
        int diameter = service.diameter(ext);

//        Zentrum
        List<Integer> center = service.center(ext);

        add(new H1("Graph Analyse Ergebnisse"));

        Div table = new Div();
        table.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr")
                .set("gap", "0px")
                .set("margin-bottom", "20px")
                .set("width", "300px")
                .set("border", "1px solid gray")
                .set("border-radius", "8px")
                .set("overflow", "hidden")
                .set("background-color", "black");

        Div header = new Div();
        header.getStyle()
                .set("display", "contents");

        Div h1 = new Div(new Text("Knoten"));
        Div h2 = new Div(new Text("Exzentrizität"));

        h1.getStyle()
                .set("font-weight", "bold")
                .set("padding", "6px")
                .set("background-color", "#333")
                .set("color", "white")
                .set("text-align", "center");

        h2.getStyle()
                .set("font-weight", "bold")
                .set("padding", "6px")
                .set("background-color", "#333")
                .set("color", "white")
                .set("text-align", "center");

        table.add(h1, h2);

        for (int i = 0; i < ext.length; i++) {

            String nodeLabel = String.valueOf((char) ('A' + i));

            Div c1 = new Div(new Text(nodeLabel));
            Div c2 = new Div(new Text(String.valueOf(ext[i])));

            c1.getStyle()
                    .set("padding", "6px")
                    .set("text-align", "center")
                    .set("border-top", "1px solid #444");

            c2.getStyle()
                    .set("padding", "6px")
                    .set("text-align", "center")
                    .set("border-top", "1px solid #444");

            table.add(c1, c2);
        }

        add(table);


        HorizontalLayout cards = new HorizontalLayout(
                createCard("Radius", radius, ext),
                createCard("Durchmesser", diameter, ext),
                createCenterCard("Zentrum", center, ext));

        cards.setSpacing(true);
        cards.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        add(cards);
    }

    private Component createCard(String title, int value, int[] ext) {

        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.15)")
                .set("min-width", "180px")
                .set("text-align", "center")
                .set("background-color", "black");

        H3 h3 = new H3(title);
        Span span = new Span(String.valueOf(value));

        span.getStyle()
                .set("font-size", "22px")
                .set("font-weight", "bold");

        Button toggle = new Button("Details");
        Div details = new Div();
        details.setVisible(false);

        Div headerRow = new Div();
        headerRow.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr")
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("border-bottom", "1px solid gray")
                .set("padding", "6px");

        headerRow.add(
                new Span("Knoten"),
                new Span("Exzentrizität")
        );

        details.add(headerRow);

        for (int i = 0; i < ext.length; i++) {
            Div row = new Div();
            row.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "1fr 1fr")
                    .set("text-align", "center")
                    .set("padding", "4px");

            row.add(
                    new Span(String.valueOf(i + 1)),
                    new Span(String.valueOf(ext[i])));

            details.add(row);
        }

        toggle.addClickListener(e -> {
            details.setVisible(!details.isVisible());
            toggle.setText(details.isVisible() ? "Details verstecken" : "Details anzeigen");
        });

        card.add(h3, span, toggle, details);

        return card;
    }

    private Component createCenterCard(String title, List<Integer> center, int[] ext) {

        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.15)")
                .set("min-width", "220px")
                .set("text-align", "center")
                .set("background-color", "black")
                .set("color", "white");

        H3 h3 = new H3(title);

        String centerText = center.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        Span span = new Span(centerText);
        span.getStyle()
                .set("font-size", "22px")
                .set("font-weight", "bold");

        Button toggle = new Button("Details");

        Div details = new Div();
        details.setVisible(false);

        // Header
        Div headerRow = new Div();
        headerRow.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr")
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("border-bottom", "1px solid gray")
                .set("padding", "6px");

        headerRow.add(
                new Span("Knoten"),
                new Span("Exzentrizität")
        );

        details.add(headerRow);

        // Tabelle
        for (int i = 0; i < ext.length; i++) {

            Div row = new Div();
            row.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "1fr 1fr")
                    .set("text-align", "center")
                    .set("padding", "4px");

            String node = String.valueOf(i + 1);

            String mark = center.contains(i) ? "✔" : "";

            row.add(
                    new Span(node + " " + mark),
                    new Span(String.valueOf(ext[i]))
            );

            details.add(row);
        }

        toggle.addClickListener(e -> {
            details.setVisible(!details.isVisible());
            toggle.setText(details.isVisible() ? "Details verstecken" : "Details anzeigen");
        });

        card.add(h3, span, toggle, details);

        return card;
    }

}

