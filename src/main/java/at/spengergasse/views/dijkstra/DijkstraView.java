package at.spengergasse.views.dijkstra;

import at.spengergasse.model.Graph;
import at.spengergasse.service.DijkstraResult;
import at.spengergasse.service.DijkstraService;
import at.spengergasse.util.GraphDataMapper;
import at.spengergasse.views.upload.UploadView;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Dijkstra")
@Route("dijkstra")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOLT_SOLID)
public class DijkstraView extends VerticalLayout {

    private final DijkstraService service = new DijkstraService();
    private int[][] matrix;

    private Div content;
    private int selectedNode = -1;

    public DijkstraView() {
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);

        setSizeFull();

        Graph graphModel = (Graph) VaadinSession.getCurrent().getAttribute("graph");

        if (graphModel == null) {
            H2 message = new H2(" Kein Graph vorhanden! Bitte zuerst Upload durchfüren.");

            Button uploadButton = new Button("Zum Upload", e ->
                    UI.getCurrent().navigate(UploadView.class));

            HorizontalLayout header = new HorizontalLayout(message, uploadButton);
            header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            add(header);
            return;
        }

        matrix = graphModel.toMatrixArray();

        Div card = createCard();
        Div graph = createGraphContainer();


        card.setWidth("320px");
        card.getStyle().set("flex-shrink", "0");

        HorizontalLayout layout = new HorizontalLayout(card, graph);
        layout.setSizeFull();

        layout.setAlignItems(Alignment.START);

        layout.setFlexGrow(1, graph);
        layout.setFlexGrow(0, card);

        add(layout);

        addAttachListener(e -> initGraph(graph));
    }

    // ================= GRAPH INIT =================

    private void initGraph(Div graphContainer) {

        String nodes = GraphDataMapper.buildNodes(matrix.length);
        String edges = GraphDataMapper.buildEdges(matrix);

        UI.getCurrent().getPage().addJavaScript(
                "https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"
        );

        UI.getCurrent().getPage().executeJs("""
            const container = $0;

            function start() {

                if (!window.vis || !window.vis.Network) return;
                if (window.network) return;

                const rawNodes = JSON.parse($1);

                window.nodes = new vis.DataSet(rawNodes);
                window.edges = new vis.DataSet(JSON.parse($2));

                window.network = new vis.Network(container, {
                    nodes: window.nodes,
                    edges: window.edges
                }, {
                    physics: true
                });

                window.network.on("click", (params) => {
                    if (params.nodes.length > 0) {
                        const id = params.nodes[0];
                        $3.$server.nodeSelected(id);
                    }
                });
            }

            const interval = setInterval(() => {
                if (window.vis && window.vis.Network) {
                    clearInterval(interval);
                    start();
                }
            }, 50);
        """,
                graphContainer.getElement(),
                nodes,
                edges,
                getElement());
    }

    // ================= SERVER CALL =================

    @ClientCallable
    public void nodeSelected(int nodeId) {

        selectedNode = nodeId;

        DijkstraResult result = service.dijkstra(matrix, nodeId);

        getUI().ifPresent(ui ->
                ui.access(() -> updateCard(result))
        );
    }

    // ================= UI OUTPUT =================

    private void updateCard(DijkstraResult result) {

        if (content == null) return;

        content.removeAll();

        int[] dist = result.getDistances();
        int[] prev = result.getPrevious();

        // ================= HEADER =================
        Div header = new Div();

        header.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr 1fr")
                .set("width", "100%")
                .set("justify-items", "center")
                .set("align-items", "center")
                .set("font-weight", "bold");

        header.add(
                new Div("Knoten"),
                new Div("Vorgänger"),
                new Div("Distanz")
        );

        content.add(header);

        // ================= TABLE ROWS =================
        for (int i = 0; i < dist.length; i++) {

            String node = getLabel(i);

            String predecessor = (prev[i] == -1)
                    ? "–"
                    : getLabel(prev[i]);

            String distance = (dist[i] == Integer.MAX_VALUE)
                    ? "∞"
                    : String.valueOf(dist[i]);

            Div row = new Div();

            row.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "1fr 1fr 1fr")
                    .set("text-align", "center")
                    .set("width", "100%")
                    .set("font-family", "monospace")
                    .set("padding", "2px 0");

            row.add(
                    new Div(node),
                    new Div(predecessor),
                    new Div(distance)
            );

            content.add(row);
        }

        Div start = new Div("Startknoten: " + getLabel(selectedNode));

        start.getStyle()
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("margin-bottom", "12px")
                .set("color", "#00aaff");

        content.add(start);

        // ================= PATHS =================
        content.add(new Div(" "));

        Div title = new Div("Alle kürzesten Pfade vom Startknoten");
        title.getStyle()
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("margin-top", "10px");

        content.add(title);

        content.add(new Div(" "));

        for (int i = 0; i < dist.length; i++) {

            String path = buildPath(result, i);

            String distance = (dist[i] == Integer.MAX_VALUE)
                    ? "∞"
                    : String.valueOf(dist[i]);

            Div pathRow = new Div(getLabel(i) + ": " + path + "   (Distanz: " + distance + ")");

            pathRow.getStyle()
                    .set("text-align", "center")
                    .set("width", "100%")
                    .set("font-family", "monospace");

            content.add(pathRow);
        }
    }

    // ================= PATH =================

    private String buildPath(DijkstraResult result, int target) {

        List<Integer> path = result.getPathTo(target);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < path.size(); i++) {

            if (i > 0) {
                sb.append(" → ");
            }

            sb.append(getLabel(path.get(i)));
        }

        return sb.toString();
    }

    // ================= UI HELPERS =================

    private Div createCard() {

        Div card = new Div();

        card.getStyle()
                .set("width", "100%")
                .set("padding", "16px")
                .set("background", "#111")
                .set("color", "white")
                .set("border-radius", "12px");

        content = new Div();

        card.add(
                new Div("Dijkstra"),
                new Div("👉 Knoten auswählen"),
                content
        );

        return card;
    }

    private Div createGraphContainer() {

        Div graph = new Div();
        graph.setWidth("100%");
        graph.setHeight("500px");

        graph.getStyle().set("border", "1px solid #ccc");

        return graph;
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