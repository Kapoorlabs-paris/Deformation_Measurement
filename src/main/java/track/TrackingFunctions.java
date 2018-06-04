package track;

import java.util.ArrayList;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import pluginTools.InteractiveSimpleEllipseFit;

public class TrackingFunctions {

	
	final InteractiveSimpleEllipseFit parent;
	
	public TrackingFunctions(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
		
	}
	
	public SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge > Trackfunction() {
		
		parent.UserchosenCostFunction = new ETrackCostFunction(1, 0);
		
		IntersectionobjectCollection coll = new IntersectionobjectCollection();
		
		for(Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLIntersections.entrySet()) {
			
			String ID = entry.getKey();
			ArrayList<Intersectionobject> bloblist = entry.getValue();
		
			for (Intersectionobject blobs: bloblist) {
				
				
				coll.add(blobs, ID);
				System.out.println(blobs.Intersectionpoint[0] + " " + blobs.Intersectionpoint[1] + " To track");
			}
			
		}
		
		
		KFsearch Tsearch = new KFsearch(coll, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius, parent.maxframegap, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph< Intersectionobject, DefaultWeightedEdge > simplegraph = Tsearch.getResult();
		
		
		return simplegraph;
		
		
	}
	
}
