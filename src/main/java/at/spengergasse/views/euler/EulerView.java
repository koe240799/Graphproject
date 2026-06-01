package at.spengergasse.views.euler;

import at.spengergasse.model.Graph;
import at.spengergasse.service.EulerService;
import at.spengergasse.views.upload.UploadView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Euler")
@Route("euler")
@Menu(order = 3, icon = LineAwesomeIconUrl.GRADUATION_CAP_SOLID)
public class EulerView extends VerticalLayout {

//    Service enthält die Logik für Analyse
    private final EulerService service = new EulerService();

    public EulerView() {
        setSpacing(true);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);


        // Graph aus Upload holen
        Graph graphModel = (Graph) VaadinSession.getCurrent().getAttribute("graph");

        // Fehlermeldung wenn kein Graph vorhanden ist
        if (graphModel == null) {

            H2 message = new H2("Kein Graph vorhanden! Bitte zuerst Upload durchführen.");

            Button uploadButton = new Button("Zum Upload",
                    e -> UI.getCurrent().navigate(UploadView.class));

            add(message, uploadButton);

            setDefaultHorizontalComponentAlignment(Alignment.CENTER);
            return;
        }

//        Erzeugung Adjazenzmatrix
        int[][] matrix = graphModel.toMatrixArray();

        add(new H1("Eulersche Linien & Zyklen"));

        Div container = new Div();
        container.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("gap", "15px")
                .set("width", "100%")
                .set("max-width", "600px")
                .set("margin", "0 auto");


//        Service entscheidet: cycle, path oder none
        String type = service.getEulerType(matrix);

        switch (type) {
            case "cycle":
                List<String> cycle = service.getCycle(matrix);
                container.add(createCycleCard(cycle));
                break;
            case "path":
                List<String> path = service.getPath(matrix);
                container.add(createPathCard(path));
                break;

            default:
                container.add(createInfoCard(
                        "Kein Eulerweg",
                        "Mehr als zwei Knoten haben ungeraden Grad."
                ));
        }
        add(container);
    }

    private Div createPathCard(List<String> path) {

        Div card = new Div();

        card.getStyle()
                .set("padding", "12px")
                .set("background-color", "black")
                .set("color", "white")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("margin-top", "10px");

        card.add(new H3("Eulersche Linie"));

        Div content = new Div();

        List<String> mapped = path.stream()
                .map(Integer::parseInt)
                .map(this::getLabel)
                .toList();

        content.setText(String.join(" → ", mapped));

        card.add(content);

        return card;
    }


    // 🟡 Info Card (Fehler / Hinweis)
    private Div createInfoCard(String title, String text) {

        Div card = new Div();

        card.getStyle()
                .set("padding", "12px")
                .set("background-color", "black")
                .set("color", "white")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("margin-top", "10px");

        card.add(
                new H3(title),
                new Div(text)
        );

        return card;
    }

    // 🟢 Zyklus Card
    private Div createCycleCard(List<String> cycle) {

        Div card = new Div();

        card.getStyle()
                .set("padding", "12px")
                .set("background-color", "black")
                .set("color", "white")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("margin-top", "10px");

        card.add(new H3("Eulerscher Zyklus"));

        Div content = new Div();

        List<String> mapped = cycle.stream()
                .map(Integer::parseInt)
                .map(this::getLabel)
                .toList();

        content.setText(String.join(" → ", mapped));

        card.add(content);

        return card;
    }

    private String getLabel(int i) {

        StringBuilder sb = new StringBuilder();

        while (i >= 0) {
            sb.insert(0, (char) ('A' + (i % 26)));
            i = i / 26 - 1;
        }

        return sb.toString();
    }
}