package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
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

public class CurvatureFinderDistance<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T> implements CurvatureFinders<T> {

	
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
			final int celllabel, final int thirdDimension, final int fourthDimension ) {
		
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

		getCurvature(parent, Ordered.getB(), centerpoint, ndims, celllabel, thirdDimension, fourthDimension);
		
		
		return true;
	}

	@Override
	public String getErrorMessage() {
		
		return errorMessage;
	}

	
	/**
	 * 
	 * Take in a list of ordered co-ordinates and compute a curvature object
	 * containing the curvature information at each co-ordinate Makes a tree
	 * structure of the list
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @return
	 */
	public RegressionCurveSegment getCurvature(InteractiveSimpleEllipseFit parent,	List<RealLocalizable> truths, RealLocalizable centerpoint,
			 int ndims, int Label,  int z, int t) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();

		ArrayList<double[]> totalinterpolatedCurvature = new ArrayList<double[]>();

		ArrayList<RegressionFunction> totalfunctions = new ArrayList<RegressionFunction>();

		double perimeter = 0;

		if (parent.fourthDimensionSize > 1)
			parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
					parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		MakeSegments(parent, truths, parent.minNumInliers, celllabel);
		// Now do the fitting
		ArrayList<Segmentobject> Allcellsegment = new ArrayList<Segmentobject>();
		for (Map.Entry<Integer, List<RealLocalizable>> entry : parent.Listmap.entrySet()) {
			
			
			List<RealLocalizable> sublist = entry.getValue();
			/***
			 * 
			 * Main method that fitst on segments a function to get the curvature
			 * 
			 */
			Pair<RegressionFunction, ArrayList<double[]>> localfunction = FitonList(parent, centerpoint, sublist);

			perimeter += localfunction.getA().Curvaturepoints.get(0)[3];
			totalfunctions.add(localfunction.getA());
			totalinterpolatedCurvature.addAll(localfunction.getB());

			RealLocalizable Cord = Listordereing.getMeanCord(sublist);
			double Curvature = localfunction.getB().get(0)[2];
			double IntensityA = localfunction.getB().get(0)[4];
			double IntensityB = localfunction.getB().get(0)[5];
			double SegPeri = localfunction.getB().get(0)[3];

			Iterator<RealLocalizable> iter = sublist.iterator();
			ArrayList<double[]> curvelist = new ArrayList<double[]>();
			while (iter.hasNext()) {

				RealLocalizable current = iter.next();

				curvelist.add(new double[] { current.getDoublePosition(0), current.getDoublePosition(1), Curvature,
						IntensityA, IntensityB });
			}

			Segmentobject cellsegment = new Segmentobject(curvelist, centerpoint, Cord, Curvature, IntensityA,
					IntensityB, SegPeri, entry.getKey(), Label, z, t);

			Allcellsegment.add(cellsegment);

		}

		for (int indexx = 0; indexx < totalinterpolatedCurvature.size(); ++indexx) {

			Curvatureobject currentobject = new Curvatureobject(
					totalinterpolatedCurvature.get(indexx)[2], perimeter, totalinterpolatedCurvature.get(indexx)[4],
					totalinterpolatedCurvature.get(indexx)[5], Label, new double[] {
							totalinterpolatedCurvature.get(indexx)[0], totalinterpolatedCurvature.get(indexx)[1] },
					z, t);

			curveobject.add(currentobject);

		}

		// All nodes are returned

	
		
		
		RegressionCurveSegment resultpair = new RegressionCurveSegment(totalfunctions, curveobject, Allcellsegment);
		Bestdelta.put(0, resultpair);
		parent.localCurvature = resultpair.Curvelist;
		parent.functions = resultpair.functionlist;
		parent.localSegment = resultpair.Seglist;
		
		Pair<Intersectionobject, Intersectionobject> sparseanddensepair = GetAverage(parent, centerpoint, Bestdelta,
				1);

		AllCurveintersection.add(sparseanddensepair.getA());
		AlldenseCurveintersection.add(sparseanddensepair.getB());

		parent.AlllocalCurvature.add(parent.localCurvature);
		
		
		
		
		return resultpair;

	}
	
	

	@Override
	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint) {
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

		Pair<RegressionFunction, ArrayList<double[]>> Curvaturedistancelist = DistanceCurvatureBlock(list, centerpoint);
		
		
		
		
		return Curvaturedistancelist;
	}
	
	
	public Pair<RegressionFunction, ArrayList<double[]>> DistanceCurvatureBlock(final ArrayList<RealLocalizable> pointlist,
			RealLocalizable centerpoint) {
		
		
		double Kappa = 0;
		double perimeter = 0;
		int ndims = centerpoint.numDimensions();
		double meanIntensity = 0;
		double meanSecIntensity = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();
		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];
		perimeter = Distance.DistanceSqrt(pointlist.get(0), pointlist.get(pointlist.size() - 1));
		perimeter = perimeter * parent.calibration;
		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];

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
			
			Kappa = getDistance(point, centerpoint);
			for (int d = 0; d < newpos.length; ++d)
				longnewpos[d] = (long) newpos[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);
			long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0), (long)centerpoint.getDoublePosition(1) };
			net.imglib2.Point centpos = new net.imglib2.Point(centerloc);
			Pair<Double, Double> Intensity = getIntensity(parent,intpoint, centpos);
			// Average the intensity.
			meanIntensity += Intensity.getA();
			meanSecIntensity += Intensity.getB();
			
			
			
			AllCurvaturepoints.add(
					new double[] { newpos[0], newpos[1], Math.max(0,Kappa), perimeter, Intensity.getA(), Intensity.getB() });
			
		}
		
		
		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.max(0,Kappa), perimeter, meanIntensity, meanSecIntensity });
		
		RegressionFunction finalfunctionransac = new RegressionFunction(Curvaturepoints);
		
		return new ValuePair<RegressionFunction, ArrayList<double[]>>(finalfunctionransac, AllCurvaturepoints);
		
	}
	


	@Override
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z) {
		// TODO Auto-generated method stub
		
	}

}
