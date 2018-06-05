package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JProgressBar;

import curvatureUtils.DisplaySelected;
import curvatureUtils.InterpolateCurvature;
import curvatureUtils.PointExtractor;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
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
import ransacPoly.RegressionFunction;


public class LabelCurvature implements Runnable {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<FloatType> ActualRoiimg;
	List<RealLocalizable> truths;
	final int t;
	final int z;
	final int celllabel;
	final int percent;
	final ArrayList<Line> resultlineroi;
	final ArrayList<OvalRoi> resultcurvelineroi;
	final ArrayList<OvalRoi> resultallcurvelineroi;
	final JProgressBar jpb;
	ArrayList<Intersectionobject> AllCurveintersection;
	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, List<RealLocalizable> truths,
			ArrayList<Line> resultlineroi, ArrayList<OvalRoi> resultcurvelineroi, ArrayList<OvalRoi> resultallcurvelineroi,ArrayList<Intersectionobject> AllCurveintersection, final int t, final int z, final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.AllCurveintersection = AllCurveintersection;
		this.jpb = null;
		this.percent = 0;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.resultallcurvelineroi = resultallcurvelineroi;
		this.celllabel = celllabel;
	}

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, List<RealLocalizable> truths,
			ArrayList<Line> resultlineroi, ArrayList<OvalRoi> resultcurvelineroi,ArrayList<OvalRoi> resultallcurvelineroi, ArrayList<Intersectionobject> AllCurveintersection, final int t, final int z, final JProgressBar jpb, final int percent,
			final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.resultallcurvelineroi = resultallcurvelineroi;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = jpb;
		this.percent = percent;
		this.AllCurveintersection = AllCurveintersection;
		this.celllabel = celllabel;
	}

	@Override
	public void run() {
		
		parent.Allnodes.clear();
		parent.Nodemap.clear();
		
		
			if (parent.fourthDimensionSize != 0 && parent.Accountedframes.size()!=0 && parent.Accountedframes!=null)
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.Accountedframes.entrySet().size()),
						"Computing Curvature = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
								+ parent.thirdDimensionSize);
			else if(parent.thirdDimensionSize!=0 && parent.AccountedZ.size()!=0 && parent.AccountedZ!=null)
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
						"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		 else {

			utility.ProgressBar.SetProgressBar(jpb, 100 ,
					"Computing Curvature ");
		}
		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);
		// Get the sparse list of points
		List<RealLocalizable> allorderedtruths = Listordereing.getOrderedList(truths);
		
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);
		
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
		
		
		Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> resultpair  = CurvatureFunction.getCurvature( parent,
				allorderedtruths, parent.insideCutoff, parent.minNumInliers,
				 ndims, celllabel, parent.degree, t, z);
		parent.localCurvature = resultpair.getB();
		parent.functions = resultpair.getA();
		// Make intersection object here

		
		Intersectionobject currentobject = PointExtractor.CurvaturetoIntersection(parent.localCurvature, parent.functions, centerpoint);
		
		
		AllCurveintersection.add(currentobject);
		String uniqueID = Integer.toString(z) + Integer.toString(t);

		resultlineroi.addAll(currentobject.linerois);
		resultcurvelineroi.addAll(currentobject.curvelinerois);
		resultallcurvelineroi.addAll(currentobject.curvealllinerois);
		
		Roiobject currentroiobject = new Roiobject(null, resultallcurvelineroi, resultlineroi, resultcurvelineroi, z, t, celllabel, true);
		parent.ZTRois.put(uniqueID, currentroiobject);
		DisplayAuto.Display(parent);
		parent.AlllocalCurvature.add(parent.localCurvature);


	}

}
