package track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import kalmanForSegments.ETrackSegmentCostFunction;
import kalmanForSegments.KFSegmentsearch;
import kalmanForSegments.Segmentobject;
import kalmanForSegments.SegmentobjectCollection;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import pluginTools.InteractiveSimpleEllipseFit;

public class TrackingFunctions {

	final InteractiveSimpleEllipseFit parent;

	public TrackingFunctions(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;

	}

	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> Trackfunction() {

		parent.UserchosenCostFunction = new ETrackCostFunction(1, 0);

		ArrayList<ArrayList<Intersectionobject>> colllist = new ArrayList<ArrayList<Intersectionobject>>();
		parent.ALLIntersections = hashMapSorter.SortTimeorZ.sortByIntegerInter(parent.ALLIntersections);
		for (Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLIntersections.entrySet()) {

			ArrayList<Intersectionobject> bloblist = entry.getValue();
			colllist.add(bloblist);
		

		}

		KFsearch Tsearch = new KFsearch(colllist, parent.UserchosenCostFunction,  parent.maxSearchradius ,
				 parent.maxSearchradius, 
				parent.thirdDimensionSize / 4, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;

	}

	public SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> TrackSegmentfunction() {

		parent.UserchosenSegmentCostFunction = new ETrackSegmentCostFunction(1, 0);

		ArrayList<ArrayList<Segmentobject>> colllist = new ArrayList<ArrayList<Segmentobject>>();
		parent.ALLSegments = hashMapSorter.SortTimeorZ.sortByInteger(parent.ALLSegments);
		
		
		
		for (Map.Entry<String, ArrayList<Segmentobject>> entry : parent.ALLSegments.entrySet()) {

			ArrayList<Segmentobject> bloblist = entry.getValue();
			
			colllist.add(bloblist);
		
		}
		
		
	
		
		

		KFSegmentsearch Tsearch = new KFSegmentsearch(colllist, parent.UserchosenSegmentCostFunction, parent.maxSearchradius ,
				 parent.maxSearchradius, 
				parent.thirdDimensionSize / 4, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;

	}

}
