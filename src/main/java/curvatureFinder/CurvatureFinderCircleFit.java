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
	public final ArrayList<Intersectionobject> AlldenseCurveintersection;
	HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	private final String BASE_ERROR_MSG = "[CircleFit-]";
	protected String errorMessage;

	public CurvatureFinderCircleFit(final InteractiveSimpleEllipseFit parent,
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Intersectionobject> AlldenseCurveintersection,
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
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z) {

		// Get the sparse list of points
	     

		int count = 0;
		if (parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();

		int i = parent.increment;
		RegressionCurveSegment resultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t, z);
        
		ArrayList<LineProfileCircle> zeroline = resultpair.LineScanIntensity;
		
		
		int maxstride = parent.CellLabelsizemap.get(celllabel);
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
		AlldenseCurveintersection.add(sparseanddensepair.getB());
		
		
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

		RegressionLineProfile finalfunctionandList = RansacEllipseBlock(list, centerpoint, centerpoint.numDimensions(), strideindex);

		
		return finalfunctionandList;
	}
	
	
	
	
	
	
	/**
	 * 
	 * Fit an ellipse to a bunch of points
	 * 
	 * @param pointlist
	 * @param ndims
	 * @return
	 */

	public RegressionLineProfile RansacEllipseBlock(final ArrayList<RealLocalizable> pointlist,
			RealLocalizable centerpoint, int ndims, int strideindex) {

		final RansacFunctionEllipsoid ellipsesegment = FitLocalEllipsoid.findLocalEllipsoid(pointlist, ndims);

		
		
		
		double Kappa = 0;
		double perimeter = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();

		double radii = ellipsesegment.function.getRadii();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];
		for (int i = 0; i < pointlist.size() - 1; ++i) {
			
			perimeter += Distance.DistanceSqrt(pointlist.get(i), pointlist.get(i + 1));
				
			}
			
			perimeter = perimeter * parent.calibration;
		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];
		long[] longpointB = new long[ndims];
		
		double meanIntensity = 0;
		double meanSecIntensity = 0;
		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointA[i] = pointlist.get(0).getDoublePosition(i);
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);
			pointC[i] = pointlist.get(size - 1).getDoublePosition(i);

		}
		
		
		for (RealLocalizable point : pointlist) {

			point.localize(newpos);

			Kappa = 1.0 / (radii * parent.calibration);
			for (int d = 0; d < newpos.length; ++d)
				longnewpos[d] = (long) newpos[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

			long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0),
					(long) centerpoint.getDoublePosition(1) };
			net.imglib2.Point centpos = new net.imglib2.Point(centerloc);

			Pair<Double, Double> Intensity = getIntensity(parent, intpoint, centpos);

			
				
			// Average the intensity.
			meanIntensity += Intensity.getA();
			meanSecIntensity += Intensity.getB();

			AllCurvaturepoints.add(new double[] { newpos[0], newpos[1], Math.max(0,Kappa), perimeter, Intensity.getA(),
					Intensity.getB() });
		}
		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.max(0,Kappa), perimeter, meanIntensity, meanSecIntensity });

		RegressionFunction finalfunctionransac = new RegressionFunction(ellipsesegment.function, Curvaturepoints);

		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>();
	
			if (strideindex == 0) {
			for (int d = 0; d < newpos.length; ++d)
				longpointB[d] = (long) pointB[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longpointB);
			
			
		LinefunctionCircle NormalLine = new LinefunctionCircle(ellipsesegment.function, intpoint);
		
		double[] NormalSlopeIntercept = NormalLine.NormalatPoint();
		
		double startNormalX = intpoint.getDoublePosition(0) - parent.insidedistance ;
		double startNormalY = NormalSlopeIntercept[0] * startNormalX + NormalSlopeIntercept[1];
		
		double endNormalX = intpoint.getDoublePosition(0) + parent.insidedistance;
		double endNormalY = NormalSlopeIntercept[0] * endNormalX + NormalSlopeIntercept[1];
		
		long[] startNormal = { (long)startNormalX, (long)startNormalY };
		
		long[] midNormal = {(long)intpoint.getDoublePosition(0), (long)intpoint.getDoublePosition(1)};
		
		long[] endNormal = { (long)endNormalX, (long)endNormalY};
		
		LineScanIntensity = getLineScanIntensity(parent, startNormal, midNormal, endNormal, NormalSlopeIntercept[0], NormalSlopeIntercept[1]);

			}
	
	
		
		
		
		
		
		
		
		RegressionLineProfile currentprofile = new RegressionLineProfile(finalfunctionransac, LineScanIntensity, AllCurvaturepoints);
		
		return currentprofile;
	}
	
	
	public ArrayList<LineProfileCircle> getLineScanIntensity(final InteractiveSimpleEllipseFit parent, final long[] startNormal, final long[] mindNormal, final long[] endNormal, final double slope, final double intercept){
		
		int count = 0;
		
		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>();
		
		
		RandomAccess<FloatType> ranac = parent.CurrentViewOrig.randomAccess();

		
		long minXdim = parent.CurrentViewOrig.min(0);
		long minYdim = parent.CurrentViewOrig.min(1);
		
		long maxXdim = parent.CurrentViewOrig.max(0);
		long maxYdim = parent.CurrentViewOrig.max(1);
		
		
		double Intensity = 0;
		double IntensitySec = 0;
		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		ranac.setPosition(startNormal);
		ranacsec.setPosition(ranac);
		
		Intensity = ranac.get().get();
		IntensitySec = ranacsec.get().get();
		
		LineProfileCircle linescan = new LineProfileCircle(count, Intensity, IntensitySec);
		LineScanIntensity.add(linescan);
		
		double minX = (startNormal[0] < endNormal[0])? startNormal[0]:endNormal[0];
		double maxX = (startNormal[0] > endNormal[0])? startNormal[0]:endNormal[0];
		
		while(true) {
			
			count++;
			double nextX =  (minX + 1);
			double nextY =  (slope * nextX + intercept);
			if(nextX > minXdim && nextX < maxXdim && nextY > minYdim && nextY < maxYdim) {
			
			ranac.setPosition(new long[] {Math.round(nextX), Math.round(nextY)});
			ranacsec.setPosition(ranac);
			
			Intensity = ranac.get().get();
			IntensitySec = ranacsec.get().get();
			
			}
			
			minX = nextX;
			
			
			linescan = new LineProfileCircle(count, Intensity, IntensitySec);
			LineScanIntensity.add(linescan);
			
			if (nextX > maxX)
				break;
		}
		
		return LineScanIntensity;
		
	}

}
