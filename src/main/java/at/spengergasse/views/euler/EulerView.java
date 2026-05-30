package at.spengergasse.views.euler;

import at.spengergasse.model.Graph;
import at.spengergasse.service.EulerService;
import at.spengergasse.views.upload.UploadView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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

    private final EulerService service = new EulerService();

    public EulerView() {

        setSizeFull();

        // 🔥 Graph aus Upload holen (wie BFS)
        Graph graphModel = (Graph) VaadinSession.getCurrent().getAttribute("graph");

        // ❌ Kein Graph vorhanden
        if (graphModel == null) {

            H2 message = new H2("Kein Graph vorhanden! Bitte zuerst Upload durchführen.");

            Button uploadButton = new Button("Zum Upload",
                    e -> UI.getCurrent().navigate(UploadView.class));

            add(message, uploadButton);

            setDefaultHorizontalComponentAlignment(Alignment.CENTER);
            return;
        }

        // 📌 Titel
        add(new H2("Eulersche Linien & Zyklen"));

        // 📊 Matrix holen
        int[][] matrix = graphModel.toMatrixArray();

        // ❌ Kein eulerscher Zyklus
        if (!service.hasEulerCycle(matrix)) {

            add(createInfoCard(
                    "Kein eulerscher Zyklus",
                    "Nicht alle Knoten haben geraden Grad."
            ));
            return;
        }

        // ✅ Zyklus berechnen
        List<String> cycle = service.getCycle(matrix);

        // 📦 Ausgabe
        add(createCycleCard(cycle));
    }

    // 🟡 Info Card (Fehler / Hinweis)
    private Div createInfoCard(String title, String text) {

        Div card = new Div();

        card.getStyle()
                .set("padding", "12px")
                .set("border", "2px solid orange")
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
                .set("border", "2px solid green")
                .set("border-radius", "8px")
                .set("margin-top", "10px");

        card.add(new H3("Eulerscher Zyklus"));

        Div content = new Div();
        content.setText(String.join(" → ", cycle));

        card.add(content);

        return card;
    }
}