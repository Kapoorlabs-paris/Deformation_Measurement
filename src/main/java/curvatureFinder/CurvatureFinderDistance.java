package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import kalmanForSegments.Segmentobject;
import mpicbg.models.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.Listordereing;

public class CurvatureFinderDistance<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
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
	private final String BASE_ERROR_MSG = "[DistanceMeasure-]";
	protected String errorMessage;

	public CurvatureFinderDistance(final InteractiveSimpleEllipseFit parent,
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

		OverSliderLoop(parent, Ordered.getB(), centerpoint, truths, AllCurveintersection, AlldenseCurveintersection,
				ndims, celllabel, fourthDimension, thirdDimension);

		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);

		}

		// Here you choose which method is used to detect curvature

		Pair<RegressionFunction, ArrayList<double[]>> Curvaturedistancelist = DistanceCurvatureBlock(list, centerpoint, 0);

		return Curvaturedistancelist;
	}

	public Pair<RegressionFunction, ArrayList<double[]>> DistanceCurvatureBlock(
			final ArrayList<RealLocalizable> pointlist, RealLocalizable centerpoint, int strideindex) {

		double Kappa = 0;
		double perimeter = 0;
		int ndims = centerpoint.numDimensions();
		double meanIntensity = 0;
		double meanSecIntensity = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();
		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];
		
		
		for (int i = 0; i < pointlist.size() - 1; ++i) {
			
		perimeter += Distance.DistanceSqrt(pointlist.get(i), pointlist.get(i + 1));
			
		}
		
		perimeter = perimeter * parent.calibration;
		int size = pointlist.size();
		final double[] pointB = new double[ndims];

		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);

		}
		for (RealLocalizable point : pointlist) {

			point.localize(newpos);

			Kappa = getDistance(point, centerpoint);
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

			AllCurvaturepoints.add(new double[] { newpos[0], newpos[1], Math.max(0, Kappa), perimeter, Intensity.getA(),
					Intensity.getB() });

		}

		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.max(0, Kappa), perimeter, meanIntensity, meanSecIntensity });

		RegressionFunction finalfunctionransac = new RegressionFunction(Curvaturepoints);

		return new ValuePair<RegressionFunction, ArrayList<double[]>>(finalfunctionransac, AllCurvaturepoints);

	}

	@Override
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z) {

		if (parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();

		int i = parent.increment;

		// Get the sparse list of points

		List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i);
		RegressionCurveSegment resultpair = getCurvature(parent, allorderedtruths, centerpoint, ndims, celllabel, z, t, 0);

		// Here counter the segments where the number of inliers was too low
		Bestdelta.put(0, resultpair);

		parent.localCurvature = resultpair.Curvelist;

		parent.functions = resultpair.functionlist;
		// Get the sparse list of points, skips parent.resolution pixel points

		Pair<Intersectionobject, Intersectionobject> sparseanddensepair = GetSingle(parent, centerpoint, Bestdelta);

		AllCurveintersection.add(sparseanddensepair.getA());
		AlldenseCurveintersection.add(sparseanddensepair.getB());
		parent.AlllocalCurvature.add(parent.localCurvature);

	}

}
