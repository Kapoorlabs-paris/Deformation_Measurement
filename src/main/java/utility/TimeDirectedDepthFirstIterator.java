package utility;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import ellipsoidDetector.Intersectionobject;


public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<Intersectionobject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<Intersectionobject, DefaultWeightedEdge> g, Intersectionobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Intersectionobject vertex) {
    	
    	int ts = vertex.getFeature(Intersectionobject.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Intersectionobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Intersectionobject.FRAME).intValue();
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