package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

import org.ojalgo.matrix.store.operation.GenerateApplyAndCopyHouseholderColumn;

import curvatureUtils.PointExtractor;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import kalmanForSegments.Segmentobject;
import mpicbg.models.Point;
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
import ransacPoly.RegressionFunction;
import utility.CurvatureFunction;
import utility.Curvatureobject;
import utility.Listordereing;

public class CurvatureFinderCircleFit<T extends RealType<T> & NativeType<T>> extends SegmentCreator<T>
		implements CurvatureFinders<T> {

	public final InteractiveSimpleEllipseFit parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final ArrayList<Intersectionobject> AllCurveintersection;
	public final ArrayList<Intersectionobject> AlldenseCurveintersection;
	HashMap<Integer, RegressionCurveSegment> BestDelta = new HashMap<Integer, RegressionCurveSegment>();
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

		return BestDelta;
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
		MakeSegments(parent, truths, parent.minNumInliers, celllabel);
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);

		// Get the sparse list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(truths, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);

		ComputeinSegments.OverSliderLoop(parent, Ordered.getB(), centerpoint, truths, AllCurveintersection,
				AlldenseCurveintersection, ndims, celllabel, fourthDimension, thirdDimension);
		return true;
	}

	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(InteractiveSimpleEllipseFit parent, ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint) {

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

		Pair<RegressionFunction, ArrayList<double[]>> finalfunctionandList = RansacEllipseBlock(parent, list, centerpoint, 2);

		return finalfunctionandList;

	}

	public Pair<RegressionFunction, ArrayList<double[]>> RansacEllipseBlock(InteractiveSimpleEllipseFit parent, final ArrayList<RealLocalizable> pointlist,
			RealLocalizable centerpoint, int ndims) {

		final RansacFunctionEllipsoid ellipsesegment = FitLocalEllipsoid.findLocalEllipsoid(pointlist, ndims);

		double Kappa = 0;
		double perimeter = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();

		double radii = ellipsesegment.function.getRadii();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];

		perimeter = Distance.DistanceSqrt(pointlist.get(0), pointlist.get(pointlist.size() - 1));
		perimeter = perimeter * parent.calibration;
		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];
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

			AllCurvaturepoints.add(new double[] { newpos[0], newpos[1], Math.abs(Kappa), perimeter, Intensity.getA(),
					Intensity.getB() });
		}

		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.abs(Kappa), perimeter, meanIntensity, meanSecIntensity });

		RegressionFunction finalfunctionransac = new RegressionFunction(ellipsesegment.function, Curvaturepoints);

		return new ValuePair<RegressionFunction, ArrayList<double[]>>(finalfunctionransac, AllCurvaturepoints);
	}

	
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered, RealLocalizable centerpoint, List<RealLocalizable> truths,ArrayList<Intersectionobject> AllCurveintersection,
			ArrayList<Intersectionobject> AlldenseCurveintersection, int ndims, int celllabel, int t, int z) {

				// Get the sparse list of points
				HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();

				int count = 0;
				if (parent.minNumInliers > truths.size())
					parent.minNumInliers = truths.size();
				
				int i = parent.increment;
				RegressionCurveSegment resultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t , z);
				int maxstride = parent.CellLabelsizemap.get(celllabel);

				// Get the sparse list of points, skips parent.resolution pixel points


				for (int index = 0; index < maxstride; ++index) {
					List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i + index);

					if (parent.fourthDimensionSize > 1)
						parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
								parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
					parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
							parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));

					// Make a tree of a certain depth

					int treedepth = parent.depth - 1;

					if (treedepth <= 0)
						treedepth = 0;

					CurvatureFunction computecurve = new CurvatureFunction(parent);

					resultpair = computecurve.getCurvature(allorderedtruths, centerpoint, parent.insideCutoff,
							parent.minNumInliers, ndims, celllabel, Math.abs(Math.max(parent.degree, parent.secdegree)),
							Math.abs(Math.min(parent.degree, parent.secdegree)), z, t);

					// Here counter the segments where the number of inliers was too low

					Bestdelta.put(count, resultpair);
					count++;

					parent.localCurvature = resultpair.Curvelist;

					parent.functions = resultpair.functionlist;
					parent.localSegment = resultpair.Seglist;
					// Make intersection object here

				}

				resultpair = Bestdelta.get(0);
				ArrayList<Curvatureobject> RefinedCurvature = new ArrayList<Curvatureobject>();
				ArrayList<Curvatureobject> localCurvature = resultpair.Curvelist;

				double[] X = new double[localCurvature.size()];
				double[] Y = new double[localCurvature.size()];
				double[] Z = new double[localCurvature.size()];
				double[] I = new double[localCurvature.size()];
				double[] ISec = new double[localCurvature.size()];

				ArrayList<Double> CurvePeri = new ArrayList<Double>();
				CurvePeri.add(localCurvature.get(0).perimeter);

				for (int index = 0; index < localCurvature.size(); ++index) {

					ArrayList<Double> CurveXY = new ArrayList<Double>();
					ArrayList<Double> CurveI = new ArrayList<Double>();
					ArrayList<Double> CurveISec = new ArrayList<Double>();

					X[index] = localCurvature.get(index).cord[0];
					Y[index] = localCurvature.get(index).cord[1];
					Z[index] = localCurvature.get(index).radiusCurvature;
					I[index] = localCurvature.get(index).Intensity;
					ISec[index] = localCurvature.get(index).SecIntensity;

					CurveXY.add(Z[index]);
					CurveI.add(I[index]);
					CurveISec.add(ISec[index]);
					for (int secindex = 1; secindex < count; ++secindex) {

						RegressionCurveSegment testpair = Bestdelta.get(secindex);

						ArrayList<Curvatureobject> testlocalCurvature = testpair.Curvelist;

						double[] Xtest = new double[testlocalCurvature.size()];
						double[] Ytest = new double[testlocalCurvature.size()];
						double[] Ztest = new double[testlocalCurvature.size()];
						double[] Itest = new double[testlocalCurvature.size()];
						double[] ISectest = new double[testlocalCurvature.size()];

						CurvePeri.add(testlocalCurvature.get(0).perimeter);
						for (int testindex = 0; testindex < testlocalCurvature.size(); ++testindex) {

							Xtest[testindex] = testlocalCurvature.get(testindex).cord[0];
							Ytest[testindex] = testlocalCurvature.get(testindex).cord[1];
							Ztest[testindex] = testlocalCurvature.get(testindex).radiusCurvature;
							Itest[index] = testlocalCurvature.get(testindex).Intensity;
							ISectest[index] = testlocalCurvature.get(testindex).SecIntensity;
							if (X[index] == Xtest[testindex] && Y[index] == Ytest[testindex]) {

								CurveXY.add(Ztest[testindex]);
								CurveI.add(Itest[index]);
								CurveISec.add(ISectest[index]);

							}

						}

					}
					double frequdeltaperi = localCurvature.get(0).perimeter;
					double frequdelta = Z[index];
					double intensitydelta = I[index];
					double intensitySecdelta = ISec[index];

					Iterator<Double> setiter = CurveXY.iterator();
					while (setiter.hasNext()) {

						Double s = setiter.next();

						frequdelta += s;

					}

					frequdelta /= CurveXY.size();
					Iterator<Double> perisetiter = CurvePeri.iterator();
					while (perisetiter.hasNext()) {

						Double s = perisetiter.next();

						frequdeltaperi += s;

					}

					frequdeltaperi /= CurvePeri.size();

					Iterator<Double> Iiter = CurveI.iterator();
					while (Iiter.hasNext()) {

						Double s = Iiter.next();

						intensitydelta += s;

					}

					intensitydelta /= CurveI.size();

					Iterator<Double> ISeciter = CurveISec.iterator();
					while (ISeciter.hasNext()) {

						Double s = ISeciter.next();

						intensitySecdelta += s;

					}

					intensitySecdelta /= CurveISec.size();

					Curvatureobject newobject = new Curvatureobject((float) frequdelta, frequdeltaperi, intensitydelta,
							intensitySecdelta, localCurvature.get(index).Label, localCurvature.get(index).cord,
							localCurvature.get(index).t, localCurvature.get(index).z);

					RefinedCurvature.add(newobject);
				}

				Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> Refinedresultpair = new ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>(
						resultpair.functionlist, RefinedCurvature);
				parent.localCurvature = Refinedresultpair.getB();
				parent.functions.addAll(Refinedresultpair.getA());
				// Make intersection object here

				Pair<Intersectionobject, Intersectionobject> currentobjectpair = PointExtractor.CurvaturetoIntersection(parent,
						parent.localCurvature, parent.functions, centerpoint, parent.smoothing);
				Intersectionobject densecurrentobject = currentobjectpair.getA();
				Intersectionobject sparsecurrentobject = currentobjectpair.getB();

				AlldenseCurveintersection.add(densecurrentobject);
				AllCurveintersection.add(sparsecurrentobject);

				parent.AlllocalCurvature.add(parent.localCurvature);

			}
	
	public RegressionCurveSegment CommonLoop(InteractiveSimpleEllipseFit parent,	 List<RealLocalizable> Ordered, 
			RealLocalizable centerpoint, int ndims, int celllabel, int t, int z) {

		// Get the sparse list of points
		HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();

		int count = 0;
	

		int i = parent.increment;

		// Get the sparse list of points

		List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i);

		if (parent.fourthDimensionSize > 1)
			parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
					parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		// Make a tree of a certain depth

		int treedepth = parent.depth - 1;

		if (treedepth <= 0)
			treedepth = 0;


		RegressionCurveSegment resultpair = getCurvature(parent, allorderedtruths, centerpoint, ndims, celllabel, z, t);

		// Here counter the segments where the number of inliers was too low

		Bestdelta.put(count, resultpair);
		count++;

		parent.localCurvature = resultpair.Curvelist;

		parent.functions = resultpair.functionlist;
		parent.localSegment = resultpair.Seglist;

		return resultpair;

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

		
		// Fill the node map
		// MakeTree(parent, truths, 0, Integer.toString(0), maxdepth);

		// Make sublist, fixed size approach


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

	
		
		
		RegressionCurveSegment returnSeg = new RegressionCurveSegment(totalfunctions, curveobject, Allcellsegment);
		return returnSeg;

	}
	
	/**
	 * 
	 * Function to fit on a list of points which are not tree based
	 * 
	 * @param parent
	 * @param centerpoint
	 * @param sublist
	 * @param functions
	 * @param interpolatedCurvature
	 * @param smoothing
	 * @param maxError
	 * @param minNumInliers
	 * @param degree
	 * @param secdegree
	 * @return
	 */
	public Pair<RegressionFunction, ArrayList<double[]>> FitonList(InteractiveSimpleEllipseFit parent,
			RealLocalizable centerpoint, List<RealLocalizable> sublist) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(0), sublist.get(i).getDoublePosition(1) });
		}

		Pair<RegressionFunction, ArrayList<double[]>> resultcurvature = getLocalcurvature(Cordlist, centerpoint);

		// Draw the function

		return resultcurvature;

	}
	
	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint) {
		// TODO Auto-generated method stub
		return null;
	}

}
