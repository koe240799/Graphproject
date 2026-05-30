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
        setJustifyContentMode(JustifyContentMode.START);

        Graph graph = (Graph) VaadinSession.getCurrent().getAttribute("graph");

        if (graph == null) {
            H2 message = new H2(" Kein Graph vorhanden! Bitte zuerst Upload durchführen.");


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


        HorizontalLayout cards = new HorizontalLayout(
                createExcentricityCard("Exzentrizität", ext),
                createCard("Radius", radius, ext),
                createCard("Durchmesser", diameter, ext),
                createCenterCard("Zentrum", center, ext));

        cards.setSpacing(true);
        cards.setDefaultVerticalComponentAlignment(Alignment.START);
        add(cards);
    }


    private Component createExcentricityCard(String title, int[] ext) {

        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.15)")
                .set("background-color", "black")
                .set("color", "white")
                .set("text-align", "center")
                .set("min-height", "250px")
                .set("align-content", "center")
                .set("justify-content", "center");

        H3 h3 = new H3(title);

        Span summary = new Span("Werte");
        summary.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "bold");

        Button toggle = new Button("Details");

        Div details = new Div();
        details.setVisible(false);

        // Tabelle
        Div table = new Div();
        table.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr")
                .set("margin-top", "10px")
                .set("border", "1px solid gray")
                .set("border-radius", "8px")
                .set("overflow", "hidden");

        // Header
        Div h1 = new Div(new Text("Knoten"));
        Div h2 = new Div(new Text("Exzentrizität"));

        styleHeader(h1);
        styleHeader(h2);

        table.add(h1, h2);

        // Rows
        for (int i = 0; i < ext.length; i++) {

            Div c1 = new Div(getNodeLabel(i));
            Div c2 = new Div(new Text(String.valueOf(ext[i])));

            styleCell(c1);
            styleCell(c2);

            table.add(c1, c2);
        }

        details.add(table);

        toggle.addClickListener(e -> {
            details.setVisible(!details.isVisible());
            toggle.setText(details.isVisible()
                    ? "Details verstecken"
                    : "Details anzeigen");
        });

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);

        content.add(summary, toggle, details);

        card.add(h3, content);

        return card;
    }


    private Component createCard(String title, int value, int[] ext) {

        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.15)")
                .set("min-width", "180px")
                .set("text-align", "center")
                .set("background-color", "black")
                .set("min-height", "250px")
                .set("align-content", "center")
                .set("justify-content", "center");

        H3 h3 = new H3(title);
        Span span = new Span(String.valueOf(value));

        span.getStyle()
                .set("font-size", "22px")
                .set("font-weight", "bold");

        Button toggle = new Button("Details");
        Div details = new Div();
        details.setVisible(false);

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);

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
                    new Span(getNodeLabel(i)),
                    new Span(String.valueOf(ext[i])));

            details.add(row);
        }

        toggle.addClickListener(e -> {
            details.setVisible(!details.isVisible());
            toggle.setText(details.isVisible() ? "Details verstecken" : "Details anzeigen");
        });

        content.add(span, toggle, details);
        card.add(h3, content);

        return card;
    }

    private Component createCenterCard(String title, List<Integer> center, int[] ext) {

        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.15)")
                .set("width", "100%")
                .set("background-color", "black")
                .set("color", "white")
                .set("text-align", "center")
                .set("overflow-wrap", "break-word")
                .set("min-height", "250px")
                .set("align-content", "center")
                .set("justify-content", "center");

        H3 h3 = new H3(title);

        // 🔹 Zentrum schön als Text anzeigen (ohne [])
        String centerText = "Werte";

        Span span = new Span(centerText);
        span.getStyle()
                .set("font-size", "22px")
                .set("font-weight", "bold");

        Button toggle = new Button("Details");

        Div details = new Div();
        details.setVisible(false);

        // 🔹 Wrapper für sauberes Layout (WICHTIG!)
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);

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

        // Tabelle mit ✔ für Zentrum
        for (int i = 0; i < ext.length; i++) {

            Div row = new Div();
            row.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "1fr 1fr")
                    .set("text-align", "center")
                    .set("padding", "4px");

            String node = getNodeLabel(i);
            boolean isCenter = center.contains(i);

            Span nodeSpan = new Span(node);
            Span valueSpan = new Span(String.valueOf(ext[i]));

            if (isCenter) {
                nodeSpan.getStyle()
                        .set("font-weight", "bold")
                        .set("color", "lime");

                nodeSpan.setText(node + " ✔");
            }

            row.add(nodeSpan, valueSpan);
            details.add(row);
        }

        toggle.addClickListener(e -> {
            details.setVisible(!details.isVisible());
            toggle.setText(details.isVisible()
                    ? "Details verstecken"
                    : "Details anzeigen");
        });

        // 🔥 gleiche Struktur wie andere Cards
        content.add(span, toggle, details);

        card.add(h3, content);

        return card;
    }

    private void styleHeader(Div cell) {
        cell.getStyle()
                .set("padding", "6px")
                .set("background-color", "#333")
                .set("color", "white")
                .set("font-weight", "bold")
                .set("text-align", "center");
    }

    private void styleCell(Div cell) {
        cell.getStyle()
                .set("padding", "6px")
                .set("text-align", "center")
                .set("border-top", "1px solid #444");
    }

    private String getNodeLabel(int i) {
        StringBuilder sb = new StringBuilder();
        while (i >= 0) {
            sb.insert(0, (char) ('A' + (i % 26)));
            i = i / 26 - 1;

        }
        return sb.toString();
    }
}



