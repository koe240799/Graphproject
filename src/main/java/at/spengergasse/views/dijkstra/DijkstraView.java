package at.spengergasse.views.dijkstra;

import at.spengergasse.model.Graph;
import at.spengergasse.service.DijkstraResult;
import at.spengergasse.service.DijkstraService;
import at.spengergasse.util.GraphDataMapper;
import at.spengergasse.util.GraphVisualAdapter;
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
import tools.jackson.databind.ObjectMapper;


import java.util.List;
import java.util.Map;

@PageTitle("Dijkstra")
@Route("dijkstra")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOLT_SOLID)
public class DijkstraView extends VerticalLayout {

    private final DijkstraService service = new DijkstraService();
    private int[][] matrix;

    private Div tableContent;   // ⭐ LINKS (nur Tabelle)
    private Div pathContent;    // ⭐ RECHTS UNTEN (Pfade)
    private Div graphContainer;

    private int selectedNode = -1;

    public DijkstraView() {

        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);

        setSizeFull();


        Graph graphModel = (Graph) VaadinSession.getCurrent().getAttribute("graph");

        if (graphModel == null) {

            H2 message = new H2("Kein Graph vorhanden! Bitte zuerst Upload durchführen.");

            Button uploadButton = new Button("Zum Upload",
                    e -> UI.getCurrent().navigate(UploadView.class));

            HorizontalLayout header = new HorizontalLayout(message, uploadButton);
            header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

            add(header);
            return;
        }

        matrix = graphModel.toMatrixArray();

        // ================= LEFT CARD =================
        Div tableCard = createTableCard();   // ⭐ FIX

        // ================= GRAPH + PATH CARD =================
        graphContainer = createGraphContainer();
        Div pathCard = createPathCard();     // ⭐ FIX

        graphContainer.setHeight("450px");
        graphContainer.setWidth("100%");

        pathCard.setWidth("80%");


        // LEFT COLUMN
        VerticalLayout leftSide = new VerticalLayout(tableCard);
        leftSide.setWidth("400px");
        leftSide.setPadding(false);
        leftSide.setSpacing(true);

        // RIGHT COLUMN
        VerticalLayout rightSide = new VerticalLayout(graphContainer, pathCard);
        rightSide.setSizeFull();
        rightSide.setPadding(false);
        rightSide.setSpacing(true);
        rightSide.setWidthFull();

        HorizontalLayout layout = new HorizontalLayout(leftSide, rightSide);
        layout.setSizeFull();
        layout.setAlignItems(Alignment.START);
        layout.setFlexGrow(0, leftSide);
        layout.setFlexGrow(1, rightSide);

        add(layout);

        getElement().executeJs(
                "setTimeout(() => $0.$server.initGraphClientSide(), 100);",
                getElement()
        );
    }

    // =====================================================
    // LEFT CARD (TABELLE)
    // =====================================================
    private Div createTableCard() {

        Div card = new Div();

        card.getStyle()
                .set("padding", "16px")
                .set("background", "#111")
                .set("color", "white")
                .set("border-radius", "12px");

        tableContent = new Div();

        card.add(
                new H2("Dijkstra"),
                new Div("👉 Knoten auswählen"),
                tableContent
        );

        return card;
    }

    // =====================================================
    // RIGHT BOTTOM CARD (PFADE)
    // =====================================================
    private Div createPathCard() {

        Div card = new Div();

        card.getStyle()
                .set("padding", "16px")
                .set("background", "#111")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center");

        card.setWidth("100%");

        pathContent = new Div();

        card.add(
                new H2("Alle kürzesten Pfade"),
                pathContent
        );

        return card;
    }

    // =====================================================
    // GRAPH INIT
    // =====================================================
    @ClientCallable
    public void initGraphClientSide() {

        initGraph(graphContainer);
    }

    private void initGraph(Div graphContainer) {

        List<int[]> edgesList = GraphVisualAdapter.toEdges(matrix);

        String nodes = GraphDataMapper.buildNodes(matrix.length);

        String edges;

        try {
            edges = new ObjectMapper().writeValueAsString(
                    edgesList.stream()
                            .map(e -> Map.of("from", e[0], "to", e[1]))
                            .toList()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UI.getCurrent().getPage().executeJs("""
            if (!window.visLoading) {
                window.visLoading = true;
                let script = document.createElement('script');
                script.src = "https://unpkg.com/vis-network/standalone/umd/vis-network.min.js";
                script.onload = () => window.visLoaded = true;
                document.head.appendChild(script);
            }
        """);

        UI.getCurrent().getPage().executeJs("""
            const container = $0;

            function start() {

                if (!window.visLoaded || !window.vis || !window.vis.Network) return;
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
                
                window.network.fit();

                window.network.on("click", (params) => {
                    if (params.nodes.length > 0) {
                        const id = params.nodes[0];
                        $3.$server.nodeSelected(id);
                    }
                });
            }

            const interval = setInterval(() => {
                if (window.visLoaded && window.vis && window.vis.Network) {
                    clearInterval(interval);
                    setTimeout(start, 80);
                }
            }, 50);
        """,
                graphContainer.getElement(),
                nodes,
                edges,
                getElement());
    }

    // =====================================================
    // CLICK EVENT
    // =====================================================
    @ClientCallable
    public void nodeSelected(int nodeId) {

        selectedNode = nodeId;

        DijkstraResult result = service.dijkstra(matrix, nodeId);

        getUI().ifPresent(ui ->
                ui.access(() -> updateCards(result))
        );
    }

    // =====================================================
    // UPDATE BOTH CARDS
    // =====================================================
    private void updateCards(DijkstraResult result) {
        updateTable(result);
        updatePaths(result);
    }

    // =====================================================
    // LEFT TABLE
    // =====================================================
    private void updateTable(DijkstraResult result) {

        tableContent.removeAll();

        int[] dist = result.getDistances();
        int[] prev = result.getPrevious();

        Div header = new Div();
        header.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr 1fr")
                .set("font-weight", "bold")
                .set("column-gap", "20px") ;

        header.add(
                new Div("Knoten"),
                new Div("Vorgänger"),
                new Div("Distanz")
        );

        tableContent.add(header);

        for (int i = 0; i < dist.length; i++) {

            Div row = new Div();
            row.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "1fr 1fr 1fr")
                    .set("text-align", "center")
                    .set("font-family", "monospace");

            row.add(
                    new Div(getLabel(i)),
                    new Div(prev[i] == -1 ? "–" : getLabel(prev[i])),
                    new Div(dist[i] == Integer.MAX_VALUE ? "∞" : String.valueOf(dist[i]))
            );

            tableContent.add(row);
        }
    }

    // =====================================================
    // BOTTOM PATHS
    // =====================================================
    private void updatePaths(DijkstraResult result) {

        pathContent.removeAll();

        int[] dist = result.getDistances();

        Div start = new Div("Startknoten: " + getLabel(selectedNode));
        start.getStyle()
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("margin-bottom", "12px")
                .set("margin-top", "12px")
                .set("color", "#00aaff");

        pathContent.add(start);

        for (int i = 0; i < dist.length; i++) {

            String path = buildPath(result, i);

            pathContent.add(
                    new Div(
                            getLabel(i) + ": " +
                                    path +
                                    " (Distanz: " +
                                    (dist[i] == Integer.MAX_VALUE ? "∞" : dist[i]) +
                                    ")"
                    )
            );
        }
    }

    // =====================================================
    // PATH BUILDER
    // =====================================================
    private String buildPath(DijkstraResult result, int target) {

        List<Integer> path = result.getPathTo(target);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(getLabel(path.get(i)));
        }

        return sb.toString();
    }

    // =====================================================
    // GRAPH
    // =====================================================
    private Div createGraphContainer() {
        Div graph = new Div();
        graph.setWidth("100%");
        graph.setHeight("450px");
        return graph;
    }

    // =====================================================
    // LABEL
    // =====================================================
    private String getLabel(int i) {

        StringBuilder sb = new StringBuilder();

        while (i >= 0) {
            sb.insert(0, (char) ('A' + (i % 26)));
            i = i / 26 - 1;
        }

        return sb.toString();
    }
}