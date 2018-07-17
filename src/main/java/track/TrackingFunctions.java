package track;

import java.util.ArrayList;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
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

		IntersectionobjectCollection coll = new IntersectionobjectCollection();

		for (Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLIntersections.entrySet()) {

			String ID = entry.getKey();
			ArrayList<Intersectionobject> bloblist = entry.getValue();

			for (Intersectionobject blobs : bloblist) {

				coll.add(blobs, ID);
			}

		}

		KFsearch Tsearch = new KFsearch(coll, parent.UserchosenCostFunction, parent.maxSearchradius,
				parent.initialSearchradius, parent.maxframegap, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;

	}

	public SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> TrackSegmentfunction() {

		parent.UserchosenSegmentCostFunction = new ETrackSegmentCostFunction(1, 0);

		SegmentobjectCollection coll = new SegmentobjectCollection();

		System.out.println(parent.ALLSegments.size() + " " + "Size of All segments");
		for (Map.Entry<String, Segmentobject> entry : parent.ALLSegments.entrySet()) {

			String ID = entry.getKey();
			Segmentobject blobs = entry.getValue();


				coll.add(blobs, ID);
				
				System.out.println(blobs.Curvature + " " + blobs.segmentLabel + "  " +  blobs.time  + " In Kalman loop" + " " + ID);

		}

		KFSegmentsearch Tsearch = new KFSegmentsearch(coll, parent.UserchosenSegmentCostFunction,
				parent.maxSearchradius, parent.initialSearchradius, parent.maxframegap, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;

	}

}
