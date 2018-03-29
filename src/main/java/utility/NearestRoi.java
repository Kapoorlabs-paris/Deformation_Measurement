package utility;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ellipsoidDetector.Intersectionobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;

import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.RealPoint;
import pluginTools.InteractiveSimpleEllipseFit;

public class NearestRoi {

	
	public static EllipseRoi getNearestRois(Roiobject roi, double[] Clickedpoint, final InteractiveSimpleEllipseFit parent ) {
		

		
		EllipseRoi[] Allrois = (EllipseRoi[]) roi.roilist;
		
		EllipseRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.length);
		final List<FlagNode<EllipseRoi>> targetNodes = new ArrayList<FlagNode<EllipseRoi>>(Allrois.length);
		for (int index = 0; index < Allrois.length; ++index) {

			EllipseRoi r = Allrois[index];
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<EllipseRoi>(Allrois[index]));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<EllipseRoi>> Tree = new KDTree<FlagNode<EllipseRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<EllipseRoi> Search = new NNFlagsearchKDtree<EllipseRoi>(Tree);


				final double[] source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final FlagNode<EllipseRoi> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	
	
	public static OvalRoi getNearestIntersectionRois(Roiobject roi, double[] Clickedpoint, final InteractiveSimpleEllipseFit parent ) {
		

		ArrayList<OvalRoi> Allrois = roi.resultovalroi;
		
		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			 Roi r = Allrois.get(index);
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);


				final double[] source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final FlagNode<OvalRoi> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	
public static Line getNearestLineRois(Roiobject roi, int[] clickedpoints, final InteractiveSimpleEllipseFit parent ) {
		

		ArrayList<Line> Allrois = roi.resultlineroi;
		
		Line KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<Line>> targetNodes = new ArrayList<FlagNode<Line>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			 Roi r = Allrois.get(index);
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<Line>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Line>> Tree = new KDTree<FlagNode<Line>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<Line> Search = new NNFlagsearchKDtree<Line>(Tree);


				final int[] source = clickedpoints;
				final Point sourceCoords = new Point(source);
				Search.search(sourceCoords);
				final FlagNode<Line> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	

public static Line getNearestLineRois(Roiobject roi, double[] clickedpoints, final InteractiveSimpleEllipseFit parent ) {
	

	ArrayList<Line> Allrois = roi.resultlineroi;
	
	Line KDtreeroi = null;

	final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
	final List<FlagNode<Line>> targetNodes = new ArrayList<FlagNode<Line>>(Allrois.size());
	for (int index = 0; index < Allrois.size(); ++index) {

		 Roi r = Allrois.get(index);
		 Rectangle rect = r.getBounds();
		 
		 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
		 

		targetNodes.add(new FlagNode<Line>(Allrois.get(index)));

	}

	if (targetNodes.size() > 0 && targetCoords.size() > 0) {

		final KDTree<FlagNode<Line>> Tree = new KDTree<FlagNode<Line>>(targetNodes, targetCoords);

		final NNFlagsearchKDtree<Line> Search = new NNFlagsearchKDtree<Line>(Tree);


			final double[] source = clickedpoints;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<Line> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

	}

	return KDtreeroi;
	
}



public static Intersectionobject getNearestIntersection(ArrayList<Intersectionobject> Allintersection, double[] clickedpoints, final InteractiveSimpleEllipseFit parent ) {
	

	
	Intersectionobject KDtreeroi = null;

	final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allintersection.size());
	final List<FlagNode<Intersectionobject>> targetNodes = new ArrayList<FlagNode<Intersectionobject>>(Allintersection.size());
	
	for (int index = 0; index < Allintersection.size(); ++index) {

		 Intersectionobject intersect = Allintersection.get(index);
		 
		 targetCoords.add( new RealPoint(intersect.Intersectionpoint) );
		 

		targetNodes.add(new FlagNode<Intersectionobject>(Allintersection.get(index)));

	}

	if (targetNodes.size() > 0 && targetCoords.size() > 0) {

		final KDTree<FlagNode<Intersectionobject>> Tree = new KDTree<FlagNode<Intersectionobject>>(targetNodes, targetCoords);

		final NNFlagsearchKDtree<Intersectionobject> Search = new NNFlagsearchKDtree<Intersectionobject>(Tree);


			final double[] source = clickedpoints;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<Intersectionobject> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

	}

	return KDtreeroi;
	
}
}
      