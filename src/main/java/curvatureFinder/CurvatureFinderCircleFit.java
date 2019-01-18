package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import mpicbg.models.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.ransac.RansacModels.FitLocalEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.RansacFunctionEllipsoid;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import ransacPoly.RegressionFunction;
import utility.Listordereing;

public class CurvatureFinderCircleFit<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
		implements CurvatureFinders<T> {

	public final InteractiveSimpleEllipseFit parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final ArrayList<Intersectionobject> AllCurveintersection;
	public final HashMap<Integer, Intersectionobject> AlldenseCurveintersection;
	HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	private final String BASE_ERROR_MSG = "[CircleFit-]";
	protected String errorMessage;

	public CurvatureFinderCircleFit(final InteractiveSimpleEllipseFit parent,
			ArrayList<Intersectionobject> AllCurveintersection,HashMap<Integer, Intersectionobject> AlldenseCurveintersection,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, final JProgressBar jpb, final int percent,
			final int celllabel, final int thirdDimension, final int fourthDimension) {

		this.parent = parent;
		this.AllCurveintersection = AllCurveintersection;
		this.AlldenseCurveintersection = AlldenseCurveintersection;
		this.jpb = jpb;
		this.ActualRoiimg = ActualRoiimg;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
	}

	
	public HashMap<Integer, Intersectionobject> getMap() {

		return AlldenseCurveintersection;
	}
	
	@Override
	public HashMap<Integer, RegressionCurveSegment> getResult() {

		return Bestdelta;
	}

	@Override
	public boolean checkInput() {
		if (parent.CurrentViewOrig.numDimensions() > 4) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 4D, make slices of your stack . Got "
					+ parent.CurrentViewOrig.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		
		int ndims = ActualRoiimg.numDimensions();
		String uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);

		List<RealLocalizable> truths = GetCandidatePoints.ListofPoints(parent, ActualRoiimg, jpb, percent,
				fourthDimension, thirdDimension);
		
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);

		// Get the sparse list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(truths, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);

		OverSliderLoop(parent, Ordered.getB(), centerpoint, truths, AllCurveintersection,
				AlldenseCurveintersection, ndims, celllabel, fourthDimension, thirdDimension);
		
		
		
		
		
		return true;
	}

	


	
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection, HashMap<Integer, Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z) {

		// Get the sparse list of points
	     

		int count = 0;
		if (parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();

		int i = parent.increment;
		RegressionCurveSegment resultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t, z);
        
		ArrayList<LineProfileCircle> zeroline = resultpair.LineScanIntensity;
		
		
		int maxstride = parent.CellLabelsizemap.get(celllabel);
		
		System.out.println(maxstride + " " + celllabel + " CurvatureFinderCircleFit ");
		
		// Get the sparse list of points, skips parent.resolution pixel points

		for (int index = 0; index < maxstride; ++index) {
			List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i + index);

			if (parent.fourthDimensionSize > 1)
				parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
						parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
			parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
					parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));

			resultpair = getCurvature(parent, allorderedtruths, centerpoint, ndims, celllabel, z, t, index);

			RegressionCurveSegment newresultpair = new RegressionCurveSegment(resultpair.functionlist, resultpair.Curvelist, zeroline);
			
			Bestdelta.put(count, newresultpair);
			count++;

			parent.localCurvature = newresultpair.Curvelist;
			parent.functions = newresultpair.functionlist;

		}

		Pair<Intersectionobject, Intersectionobject> sparseanddensepair = GetAverage(parent, centerpoint, Bestdelta,count);
		
		AllCurveintersection.add(sparseanddensepair.getA());
		AlldenseCurveintersection.put(celllabel, sparseanddensepair.getB());
		
		
		parent.AlllocalCurvature.add(parent.localCurvature);

	}
	

	
	

	
	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public RegressionLineProfile getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);
			pointlist.add(new Point(new double[] { x[index], y[index] }));

		}

		// Here you choose which method is used to detect curvature

		RegressionLineProfile finalfunctionandList = RansacEllipseBlock(parent, list, centerpoint, centerpoint.numDimensions(), strideindex, false);

		
		return finalfunctionandList;
	}
	
	@Override
	public RegressionLineProfile getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);
			pointlist.add(new Point(new double[] { x[index], y[index] }));

		}

		// Here you choose which method is used to detect curvature

		RegressionLineProfile finalfunctionandList = RansacEllipseBlock(parent, list, centerpoint, centerpoint.numDimensions(), strideindex, true);

		return finalfunctionandList;
	}




	
	
	
	
	
}
