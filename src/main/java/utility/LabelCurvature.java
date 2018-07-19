package utility;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JProgressBar;

import com.google.common.eventbus.AllowConcurrentEvents;

import curvatureUtils.DisplaySelected;
import curvatureUtils.PointExtractor;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import kalmanForSegments.Segmentobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.*;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;

public class LabelCurvature implements Runnable {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<FloatType> ActualRoiimg;
	List<RealLocalizable> truths;
	final int t;
	final int z;
	final int celllabel;
	int percent;
	final ArrayList<Line> resultlineroi;
	final ArrayList<OvalRoi> resultcurvelineroi;
	final ArrayList<OvalRoi> resultallcurvelineroi;
	final ArrayList<EllipseRoi> ellipselineroi;
	final ArrayList<Roi> segmentrect;
	final JProgressBar jpb;
	ArrayList<Intersectionobject> AllCurveintersection;
	ArrayList<Segmentobject> AllCurveSegments;

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, List<RealLocalizable> truths,
			ArrayList<Line> resultlineroi, ArrayList<OvalRoi> resultcurvelineroi,
			ArrayList<OvalRoi> resultallcurvelineroi,ArrayList<EllipseRoi> ellipselineroi, ArrayList<Roi> segmentrect,
			ArrayList<Intersectionobject> AllCurveintersection,ArrayList<Segmentobject> AllCurveSegments, final int t,
			final int z, final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.AllCurveintersection = AllCurveintersection;
		this.AllCurveSegments = AllCurveSegments;
		this.jpb = null;
		this.percent = 0;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.resultallcurvelineroi = resultallcurvelineroi;
		this.ellipselineroi = ellipselineroi;
		this.celllabel = celllabel;
		this.segmentrect = segmentrect;
	}

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, List<RealLocalizable> truths,
			ArrayList<Line> resultlineroi, ArrayList<OvalRoi> resultcurvelineroi,
			ArrayList<OvalRoi> resultallcurvelineroi,ArrayList<EllipseRoi> ellipselineroi,ArrayList<Roi> segmentrect,
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Segmentobject> AllCurveSegments, final int t,
			final int z, final JProgressBar jpb, final int percent, final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.resultallcurvelineroi = resultallcurvelineroi;
		this.ellipselineroi = ellipselineroi;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = jpb;
		this.percent = percent;
		this.AllCurveintersection = AllCurveintersection;
		this.AllCurveSegments = AllCurveSegments;
		this.celllabel = celllabel;
		this.segmentrect = segmentrect;
	}

	
	private void SliderLoop(
			List<RealLocalizable> Ordered, RealLocalizable centerpoint) {

		String uniqueID = Integer.toString(z) + Integer.toString(t);
		// Get the sparse list of points
		HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();
		int count = 0;
		if (parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();

		
		int i = parent.increment;
	
		

			// Get the sparse list of points

			List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i);

			if (parent.fourthDimensionSize > 1)
				parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
						parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
			parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
					parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
			final int ndims = ActualRoiimg.numDimensions();
			// Make a tree of a certain depth

			int treedepth = parent.depth - 1;

			if (treedepth <= 0)
				treedepth = 0;

			CurvatureFunction computecurve = new CurvatureFunction(parent);
			
			RegressionCurveSegment resultpair = computecurve.getCurvature(
					allorderedtruths,centerpoint, parent.insideCutoff, parent.minNumInliers, ndims, celllabel,
					Math.abs(Math.max(parent.degree, parent.secdegree)),
					Math.abs(Math.min(parent.degree, parent.secdegree)), z, t);

			// Here counter the segments where the number of inliers was too low

			Bestdelta.put(count, resultpair);
			count++;

			parent.localCurvature = resultpair.Curvelist;
			parent.functions = resultpair.functionlist;
			parent.localSegment = resultpair.Seglist;
			// Make intersection object here

			
			Intersectionobject currentobject = PointExtractor.CurvaturetoIntersection(parent.localCurvature,
					parent.functions, centerpoint, parent.smoothing);

			
		
			
			if (parent.maxperimeter >=currentobject.perimeter)
				parent.maxperimeter = (int)Math.round(currentobject.perimeter);
			
			 resultlineroi.addAll(currentobject.linerois);
			 resultcurvelineroi.addAll(currentobject.curvelinerois);
			 resultallcurvelineroi.addAll(currentobject.curvealllinerois);
					ellipselineroi.addAll(currentobject.ellipselinerois);
					segmentrect.addAll(currentobject.segmentrect);
			
		

			Roiobject currentroiobject = new Roiobject(ellipselineroi, resultallcurvelineroi, resultlineroi, resultcurvelineroi, segmentrect,
					z, t, celllabel, true);
			parent.ZTRois.put(uniqueID, currentroiobject);
			
			
			DisplayAuto.Display(parent);
		
			

			
			
			AllCurveintersection.add(currentobject);

			AllCurveSegments.addAll(resultpair.Seglist);
			parent.AlllocalCurvature.add(parent.localCurvature);
			
			
	
	}

	@Override
	public void run() {
		String uniqueID = Integer.toString(z) + Integer.toString(t);
		parent.Allnodes.clear();
		parent.Nodemap.clear();
		parent.Listmap.clear();

		if (parent.fourthDimensionSize != 0 && parent.Accountedframes.size() != 0 && parent.Accountedframes != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()), "Computing Curvature = "
					+ t + "/" + parent.fourthDimensionSize + " Z = " + z + "/" + parent.thirdDimensionSize);
		else if (parent.thirdDimensionSize != 0 && parent.AccountedZ.size() != 0 && parent.AccountedZ != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()),
					"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		else {

			utility.ProgressBar.SetProgressBar(jpb, 100 * (percent ) / (parent.pixellist.size()), "Computing Curvature ");
		}
		
		// Get the candidate points for fitting
		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);
		// A Hash map for the slider loop from reference point, 0 to incremental positions
		
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);

		// Get the sparse list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(truths);

		// Start sliding
		 SliderLoop(
				Ordered.getB(), centerpoint);

		parent.Refcord = Ordered.getA();
	
	

	}

}
