package utility;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.util.Pair;

public interface IntersectionTracker extends OutputAlgorithm< SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge >> {
	
	
		
		

}
