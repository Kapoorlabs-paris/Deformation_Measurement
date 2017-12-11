package utility;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import ellipsoidDetector.Tangentobject;

public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<Tangentobject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<Tangentobject, DefaultWeightedEdge> g, Tangentobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Tangentobject vertex) {
    	
    	int ts = vertex.getFeature(Tangentobject.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Tangentobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Tangentobject.FRAME).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}
