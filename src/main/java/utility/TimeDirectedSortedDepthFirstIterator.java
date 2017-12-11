package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import ellipsoidDetector.Tangentobject;



public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<Tangentobject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<Tangentobject, DefaultWeightedEdge> g, final Tangentobject startVertex, final Comparator<Tangentobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final Tangentobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< Tangentobject > sortedChildren = new ArrayList< Tangentobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<Tangentobject, DefaultWeightedEdge> localEdges = new HashMap<Tangentobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(Tangentobject.FRAME).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final Tangentobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(Tangentobject.FRAME).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< Tangentobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final Tangentobject child = it.next();

            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }



}
