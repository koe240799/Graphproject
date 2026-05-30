package at.spengergasse.views.dijkstra;

import at.spengergasse.model.Graph;
import at.spengergasse.service.DijkstraResult;
import at.spengergasse.service.DijkstraService;
import at.spengergasse.util.GraphDataMapper;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Dijkstra")
@Route("dijkstra")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOLT_SOLID)
public class DijkstraView extends VerticalLayout {

    private final DijkstraService service = new DijkstraService();
    private int[][] matrix;

    private Integer startNode;

    private Div card;
    private Div graph;

    public DijkstraView() {
        System.out.println("View constructor called");
        setSizeFull();
        setSpacing(true);

        Graph graphModel =(Graph) VaadinSession.getCurrent().getAttribute("graph");

        if(graphModel == null) {
            add(new H2("Kein Graph geladen! Bitte zuerst Upload durchführen."));
            return;
        }

        matrix = graphModel.toMatrixArray();

        card = createResultCard();
        graph = createGraphContainer();

        HorizontalLayout layout = new HorizontalLayout(card, graph);
        layout.setFlexGrow(1, graph);
        layout.setFlexGrow(0, card);
        add(layout);

        UI.getCurrent().beforeClientResponse(this, ctx -> initGraph());
    }

    private void initGraph() {

        String nodes = GraphDataMapper.buildNodes(matrix.length);
        String edges = GraphDataMapper.buildEdges(matrix);

        getUI().ifPresent(ui -> {

            ui.getPage().addJavaScript("https://unpkg.com/vis-network/standalone/umd/vis-network.min.js");

            ui.getPage().executeJs(
                    """
                    const container = $0;
            
                    function start() {
            
                        if (!window.vis || !window.vis.Network) {
                            console.log("vis not ready");
                            return;
                        }
            
                        if (window.network) {
                            return; // verhindert doppeltes initialisieren
                        }
            
                        // Wir speichern die ursprünglichen Daten, um die Farben später zurücksetzen zu können
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
                                const selectedNodeId = params.nodes[0];
            
                                // 1. Alle Knoten auf ihre ursprüngliche Farbe zurücksetzen
                                rawNodes.forEach(originalNode => {
                                    window.nodes.update({
                                        id: originalNode.id,
                                        color: originalNode.color || null // Falls vorher keine Farbe definiert war, auf Default zurücksetzen
                                    });
                                });
            
                                // 2. Den ausgewählten Knoten rot färben
                                window.nodes.update({
                                    id: selectedNodeId,
                                    color: {
                                        background: '#ff0000',
                                        border: '#cc0000',
                                        highlight: {
                                            background: '#ff0000',
                                            border: '#cc0000'
                                        }
                                    }
                                });
            
                                // Server-Callback aufrufen
                                $3.$server.nodeSelected(selectedNodeId);
                            } else {
                                // Klick ins Leere: Alle Knoten wieder in den Originalzustand versetzen
                                rawNodes.forEach(originalNode => {
                                    window.nodes.update({
                                        id: originalNode.id,
                                        color: originalNode.color || null
                                    });
                                });
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
                    graph.getElement(),
                    nodes,
                    edges,
                    getElement()
            );
        });
    }

    private Div createGraphContainer() {
        Div graph = new Div();
        graph.setWidth("100%");
        graph.setHeight("500px");
        graph.getStyle().set("border", "1px solid #ccc");
        return graph;
    }

    private Div createResultCard() {
        Div card = new Div();
        card.getStyle()
                .set("width", "300px")
                .set("padding", "20px")
                .set("background", "#111")
                .set("color", "white")
                .set("border-radius", "12px");

        card.add(new H2("Dijkstra"));
        card.add(new Div("Startkonoten im Graph auswählen!"));
        return card;
    }

    @ClientCallable
    public void nodeSelected(int nodeId) {

        startNode = nodeId;

        DijkstraResult result = service.dijkstra(matrix, nodeId);

        updateCard(result.getDistances());

        getUI().ifPresent(ui -> ui.getPage().executeJs(
                """
                if (!window.nodes) {
                    console.log("nodes not ready");
                    return;
                }
        
                // reset alle
                window.nodes.get().forEach(n => {
                    window.nodes.update({
                        id: n.id,
                        color: {
                            background: '#97C2FC'
                        }
                    });
                });
        
                // 🔴 markiere aktuellen
                window.nodes.update({
                    id: $0,
                    color: {
                        background: 'red',
                        border: 'darkred'
                    }
                });
        
                """,
                nodeId
        ));
    }

    private void updateCard(int[] dist) {
        card.removeAll();
        card.add(new H2("Dijkstra"));

        for (int i = 0; i < dist.length; i++) {
            card.add(new Div("Node " + i + ": " + dist[i]));
        }
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