package at.spengergasse.views.bfs;

import at.spengergasse.model.Graph;
import at.spengergasse.service.BFSResult;
import at.spengergasse.service.BFSService;
import at.spengergasse.service.BFSStep;
import at.spengergasse.util.GraphDataMapper;
import at.spengergasse.util.GraphVisualAdapter;
import at.spengergasse.views.upload.UploadView;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

@PageTitle("BFS")
@Route("bfs")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOLT_SOLID)
public class BFSView extends VerticalLayout {

    private final BFSService service = new BFSService();
    private int[][] matrix;

    private Div content;
    private int startNode = -1;

    public BFSView() {
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

        Div card = createCard();
        Div graph = createGraphContainer();

        card.setWidth("320px");

        HorizontalLayout layout = new HorizontalLayout(card, graph);
        layout.setSizeFull();
        layout.setAlignItems(Alignment.START);

        layout.setFlexGrow(0, card);
        layout.setFlexGrow(1, graph);

        add(layout);

        getElement().executeJs("""
            setTimeout(() => $0.$server.initGraphClientSide(), 100);
        """, getElement());
    }

    @ClientCallable
    public void initGraphClientSide() {

        Div graphContainer = (Div) getChildren()
                .filter(c -> c instanceof HorizontalLayout)
                .findFirst()
                .get()
                .getElement()
                .getChild(1)
                .getComponent()
                .get();

        initGraph(graphContainer);
    }

    // ================= GRAPH =================

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

            getUI().ifPresent(ui -> {

                ui.getPage().executeJs("""
                            if (!window.visLoaded) {
                                let script = document.createElement('script');
                                script.src = "https://unpkg.com/vis-network/standalone/umd/vis-network.min.js";
                                script.onload = () => window.visLoaded = true;
                                document.head.appendChild(script);
                            }
                        """);

                ui.getPage().executeJs("""
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
            });
        }
    

    // ================= CLICK =================

    @ClientCallable
    public void nodeSelected(int nodeId) {

        startNode = nodeId;

        BFSResult result = service.bfs(matrix, nodeId);

        getUI().ifPresent(ui ->
                ui.access(() -> updateCard(result))
        );
    }

    // ================= OUTPUT (Dijkstra Style) =================

    private void updateCard(BFSResult result) {

        content.removeAll();

        // ================= START =================
        Div start = new Div("Startknoten: " + getLabel(startNode));
        start.getStyle()
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("margin-bottom", "12px")
                .set("color", "#00aaff");

        content.add(start);

        // ================= HEADER =================

        Div header = new Div();

        header.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "80px 1fr 1fr")
                .set("width", "100%")
                .set("background", "#222")
                .set("color", "white")
                .set("border-radius", "6px")
                .set("margin-bottom", "6px");

        Div schritt = new Div("Schritt");
        Div discovered = new Div("Entdeckt");
        Div processed = new Div("Verarbeitet");

        schritt.getStyle()
                .set("padding", "15px")
                .set("text-align", "center");

        discovered.getStyle()
                .set("padding", "15px")
                .set("text-align", "center");

        processed.getStyle()
                .set("padding", "15px")
                .set("text-align", "center");

        header.add(schritt, discovered, processed);

        content.add(header);
        // ================= ROWS =================
        for (BFSStep step : result.getSteps()) {

            Div row = new Div();

            row.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "80px 1fr 1fr")
                    .set("column-gap", "20px")
                    .set("width", "100%")
                    .set("text-align", "center")
                    .set("font-family", "monospace")
                    .set("padding", "10px 0")
                    .set("border-bottom", "1px solid #eee");

            row.add(
                    new Div(String.valueOf(step.getStep())),
                    new Div(format(step.getDiscovered())),
                    new Div(format(step.getProcessed()))
            );

            content.add(row);
            content.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "15px");
        }
    }

    // ================= UI =================

    private Div createCard() {

        Div card = new Div();

        card.getStyle()
                .set("padding", "16px")
                .set("background", "#111")
                .set("color", "white")
                .set("border-radius", "6px");

        content = new Div();

        card.add(
                new H2("BFS"),
                new Div("👉 Knoten auswählen"),
                content
        );

        return card;
    }

    private Div createGraphContainer() {

        Div graph = new Div();
        graph.setWidth("100%");
        graph.setHeight("500px");

        return graph;
    }

    // ================= HELPERS =================

    private String format(java.util.List<Integer> nodes) {

        if (nodes == null || nodes.isEmpty()) {
            return "-";
        }

        return nodes.stream()
                .map(this::getLabel)
                .reduce((a, b) -> a + " , " + b)
                .orElse("-");
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