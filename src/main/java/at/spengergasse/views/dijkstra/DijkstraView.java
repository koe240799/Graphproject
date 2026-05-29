package at.spengergasse.views.dijkstra;

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
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Dijkstra")
@Route("dijkstra")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOLT_SOLID)
public class DijkstraView extends VerticalLayout {

    private final DijkstraService service = new DijkstraService();
    private final int[][] matrix;

    private Integer startNode;

    private final Div card;
    private final Div graph;

    public DijkstraView() {
        System.out.println("View constructor called");
        setSizeFull();
        setSpacing(true);

        matrix = new int[][]{
                {0, 2, 0},
                {2, 0, 3},
                {0, 3, 0}
        };

        card = createResultCard();
        graph = createGraphContainer();

        HorizontalLayout layout = new HorizontalLayout(card, graph);
        layout.setSizeFull();
        add(layout);

        UI.getCurrent().beforeClientResponse(this, ctx -> initGraph());
    }

    private void initGraph() {
        System.out.println("initGraph called");

        String nodes = GraphDataMapper.buildNodes(matrix.length);
        String edges = GraphDataMapper.buildEdges(matrix);

        getUI().ifPresent(ui -> {

            ui.getPage().addJavaScript("https://unpkg.com/vis-network/standalone/umd/vis-network.min.js");

            ui.getPage().executeJs(
                    """
                            console.log("JS STARTED");
                                console.log("VIS =", window.vis);
                                
                    const container = $0;
    
                    function start() {
    
                        if (!window.vis) {
                            console.log("vis not loaded yet");
                            return;
                        }
    
                        const nodes = new vis.DataSet($1);
                        const edges = new vis.DataSet($2);
    
                        const network = new vis.Network(container, {
                            nodes: nodes,
                            edges: edges
                        }, {
                            physics: true
                        });
    
                        network.on("click", (params) => {
                            if (params.nodes.length > 0) {
                                console.log("clicked", params.nodes[0]);
                                $3.$server.nodeSelected(params.nodes[0]);
                            }
                        });
                    }
    
                    const interval = setInterval(() => {
                        if (window.vis) {
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
        return card;
    }

    @ClientCallable
    public void nodeSelected(int nodeId) {

        startNode = nodeId;

        DijkstraResult result = service.dijkstra(matrix, nodeId);

        updateCard(result.getDistances());
    }

    private void updateCard(int[] dist) {
        card.removeAll();
        card.add(new H2("Dijkstra"));

        for (int i = 0; i < dist.length; i++) {
            card.add(new Div("Node " + i + ": " + dist[i]));
        }
    }
}