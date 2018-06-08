package utility;

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
	int percent;
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
		String uniqueID = Integer.toString(z) + Integer.toString(t);
		
			if (parent.fourthDimensionSize != 0 && parent.Accountedframes.size()!=0 && parent.Accountedframes!=null)
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()),
						"Computing Curvature = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
								+ parent.thirdDimensionSize);
			else if(parent.thirdDimensionSize!=0 && parent.AccountedZ.size()!=0 && parent.AccountedZ!=null)
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()),
						"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		 else {

			utility.ProgressBar.SetProgressBar(jpb, 100 ,
					"Computing Curvature ");
		}
		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);
		HashMap<Integer, Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>> Bestdelta = 
				new HashMap<Integer,Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>>();
		
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);
		
		// Get the sparse list of points
		List<RealLocalizable> Ordered = Listordereing.getOrderedList(truths);
		
		int count = 0;
		if(parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();
		
		if(parent.increment<= 0)
			parent.increment = 1;
		int increment = Ordered.size() / parent.increment;
		for(int i = 0; i < Ordered.size(); i+=increment) {
			
			if(i%(increment) == 0)
				resultlineroi.clear();
	
			if(i >= Ordered.size() - 1)
				break;
			
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
		
		
		Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> resultpair  = CurvatureFunction.getCurvature( parent,
				allorderedtruths, parent.insideCutoff, parent.minNumInliers,
				 ndims, celllabel,Math.abs(Math.max(parent.degree, parent.secdegree)), Math.abs(Math.min(parent.degree, parent.secdegree)), t, z);
		Bestdelta.put(count, resultpair);
		count++;
		
		parent.localCurvature = resultpair.getB();
		parent.functions = resultpair.getA();
		// Make intersection object here

		
		Intersectionobject currentobject = PointExtractor.CurvaturetoIntersection(parent.localCurvature, parent.functions, centerpoint, parent.smoothing);
		
		resultlineroi.addAll(currentobject.linerois);
		//resultcurvelineroi.addAll(currentobject.curvelinerois);
		//resultallcurvelineroi.addAll(currentobject.curvealllinerois);
		
		Roiobject currentroiobject = new Roiobject(null, resultallcurvelineroi, resultlineroi, resultcurvelineroi, z, t, celllabel, true);
		parent.ZTRois.put(uniqueID, currentroiobject);
		DisplayAuto.Display(parent);
		}
		Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> resultpair = Bestdelta.get(0);
		ArrayList<Curvatureobject> RefinedCurvature = new ArrayList<Curvatureobject>();
		ArrayList<Curvatureobject> localCurvature = resultpair.getB();
		
	
		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		
		
		  for (int index = 0; index < localCurvature.size(); ++index) {

			    Set<Double> CurveXY = new HashSet<Double>();
				X[index] = localCurvature.get(index).cord[0];
				Y[index] = localCurvature.get(index).cord[1];
				Z[index] = localCurvature.get(index).radiusCurvature;
				CurveXY.add(Z[index]);
				for(int i = 1; i < count; ++i) {
					
					Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> testpair = Bestdelta.get(i);
					
					ArrayList<Curvatureobject> testlocalCurvature = testpair.getB();
					
					double[] Xtest = new double[testlocalCurvature.size()];
					double[] Ytest = new double[testlocalCurvature.size()];
					double[] Ztest = new double[testlocalCurvature.size()];
					
					  for (int testindex = 0; testindex < testlocalCurvature.size(); ++testindex) {


							Xtest[testindex] = testlocalCurvature.get(testindex).cord[0];
							Ytest[testindex] = testlocalCurvature.get(testindex).cord[1];
							Ztest[testindex] = testlocalCurvature.get(testindex).radiusCurvature;
					
							if(X[index] == Xtest[testindex] && Y[index] == Ytest[testindex]) {
								
								
								CurveXY.add(Ztest[testindex]);
							}
					
					
				}
				
				
		  }
				
				double frequdelta = Z[index];
				int frequ = 0;
				double threshold = 0.9;
				Iterator<Double> setiter = CurveXY.iterator();
				
				while(setiter.hasNext()) {
					
					Double s = setiter.next();
					
					while(setiter.hasNext()) {
						
						
						Double p = setiter.next();
						
						
						if(Math.abs( p / s) <= threshold || Math.abs( s / p) <= threshold ) {
							
							frequ++;
							frequdelta = s;
						}
						
					}
					
					
				
				}
				
				
			
				
				Curvatureobject newobject = new Curvatureobject((float)frequdelta, localCurvature.get(index).perimeter, localCurvature.get(index).Label, localCurvature.get(index).cord,
						localCurvature.get(index).t, localCurvature.get(index).z);
				 System.out.println( "Best Curvature:" + (float)frequdelta + " " + "Original" + (float)Z[index] + " " + "Frequency: " + frequ + " " +  " XY " + X[index] + " " + Y[index]);
				RefinedCurvature.add(newobject);
		  }
		
	
		  Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> Refinedresultpair = new  ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>(resultpair.getA(), RefinedCurvature);
		
		parent.localCurvature = Refinedresultpair.getB();
		parent.functions.addAll(Refinedresultpair.getA());
		// Make intersection object here

		
		Intersectionobject currentobject = PointExtractor.CurvaturetoIntersection(parent.localCurvature, parent.functions, centerpoint, parent.smoothing);
		
		//resultlineroi.addAll(currentobject.linerois);
		//resultcurvelineroi.addAll(currentobject.curvelinerois);
		//resultallcurvelineroi.addAll(currentobject.curvealllinerois);
		
		//Roiobject currentroiobject = new Roiobject(null, resultallcurvelineroi, resultlineroi, resultcurvelineroi, z, t, celllabel, true);
		//parent.ZTRois.put(uniqueID, currentroiobject);
		//DisplayAuto.Display(parent);
		AllCurveintersection.add(currentobject);
		

		
		parent.AlllocalCurvature.add(parent.localCurvature);


	}

}
