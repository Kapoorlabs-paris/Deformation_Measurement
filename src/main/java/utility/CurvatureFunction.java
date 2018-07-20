package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import curvatureUtils.Node;
import ellipsoidDetector.Distance;
import kalmanForSegments.Segmentobject;
import mpicbg.models.Point;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.ransac.RansacModels.FitLocalEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.RansacFunctionEllipsoid;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransac.PointFunctionMatch.PointFunctionMatch;
import ransac.loadFiles.Tracking;
import ransacPoly.HigherOrderPolynomialFunction;
import ransacPoly.MixedPolynomial;
import ransacPoly.MixedPolynomialFunction;
import ransacPoly.RansacFunction;
import ransacPoly.RegressionFunction;
import ransacPoly.Threepointfit;

public class CurvatureFunction {

	int evendepth;

	InteractiveSimpleEllipseFit parent;

	public CurvatureFunction(InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
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
	public RegressionCurveSegment getCurvature(
			List<RealLocalizable> truths, RealLocalizable centerpoint, double maxError, int minNumInliers, int ndims,
			int Label, int degree, int secdegree, int z, int t) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		

		ArrayList<double[]> totalinterpolatedCurvature = new ArrayList<double[]>();


		ArrayList<RegressionFunction> totalfunctions = new ArrayList<RegressionFunction>();

		double perimeter = 0;

		double smoothing = parent.smoothing;

		// Fill the node map
		// MakeTree(parent, truths, 0, Integer.toString(0), maxdepth);

		// Make sublist, fixed size approach

		MakeSegments(parent, truths, Label, z);

		// Now do the fitting
		ArrayList<Segmentobject> Allcellsegment = new ArrayList<Segmentobject>();
		
		
		for (Map.Entry<Integer, List<RealLocalizable>> entry : parent.Listmap.entrySet()) {
 
			List<RealLocalizable> sublist = entry.getValue();
			Pair<RegressionFunction, ArrayList<double[]>> localfunction = FitonList(parent, centerpoint, sublist,
					smoothing, maxError, minNumInliers, degree, secdegree, Label, z);

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
			while(iter.hasNext()) {
				
				RealLocalizable current = iter.next();
				
				curvelist.add(new double[] {current.getDoublePosition(0) , current.getDoublePosition(1), Curvature, IntensityA, IntensityB});
			}
			
			Segmentobject cellsegment = new Segmentobject(curvelist, Cord, Curvature, IntensityA, IntensityB, SegPeri, entry.getKey(), Label,
					z, t);

			
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

	public int Getdepth(InteractiveSimpleEllipseFit parent) {

		int nearestk = (int) (Math.log10(parent.depth) / Math.log10(2));

		return nearestk;
	}

	public int GetMaxStringsize(InteractiveSimpleEllipseFit parent) {

		Iterator<String> iter = parent.Nodemap.keySet().iterator();

		int maxlength = 0;

		while (iter.hasNext()) {

			String s = iter.next();
			if (s.length() > maxlength)
				maxlength = s.length();

		}

		return maxlength;

	}

	public Boolean CorrectSegmentRegression(RegressionFunction Result) {

		return false;
		/*
		 * if (Result.ellipse != null) return false; if (Result.mixedfunction!=null) {
		 * double inliersize = Result.inliers.size(); double totalsize =
		 * Result.candidates.size(); if (totalsize > 0 && inliersize > 0) { double ratio
		 * = inliersize / totalsize; double threshold = 0.5; Boolean needCorrection =
		 * false;
		 * 
		 * if (ratio <= threshold) { // We need to correct
		 * 
		 * needCorrection = true;
		 * 
		 * }
		 * 
		 * 
		 * return needCorrection; }
		 * 
		 * else return false;
		 * 
		 * }
		 * 
		 * else return false;
		 */

	}

	public Pair<Boolean, Double> FitonLeftsubTree(InteractiveSimpleEllipseFit parent, RealLocalizable centerpoint,
			Node<RealLocalizable> leaf, ArrayList<double[]> interpolatedCurvature,
			ArrayList<RegressionFunction> functions, double smoothing, double maxError, int minNumInliers, int degree,
			int secdegree, int label, int time) {

		List<RealLocalizable> Leftsubtruths = leaf.leftTree;

		// Fit function on left tree

		ArrayList<double[]> LeftCordlist = new ArrayList<double[]>();
		for (int i = 0; i < Leftsubtruths.size(); ++i) {

			LeftCordlist.add(new double[] { Leftsubtruths.get(i).getDoublePosition(0),
					Leftsubtruths.get(i).getDoublePosition(1) });

		}
		Pair<RegressionFunction, ArrayList<double[]>> Leftresultcurvature = getLocalcurvature(LeftCordlist, centerpoint,
				smoothing, maxError, minNumInliers, degree, secdegree, label, time);

		// Draw the function
		double perimeter = 0;
		if (Leftresultcurvature != null) {
			functions.add(Leftresultcurvature.getA());

			interpolatedCurvature.addAll(Leftresultcurvature.getB());

			perimeter = Leftresultcurvature.getA().Curvaturepoints.get(0)[3];

			boolean leftcorrected = CorrectSegmentRegression(Leftresultcurvature.getA());

			Pair<Boolean, Double> leftpair = new ValuePair<Boolean, Double>(leftcorrected, perimeter);

			return leftpair;

		} else
			return null;

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
			RealLocalizable centerpoint, List<RealLocalizable> sublist, double smoothing, double maxError,
			int minNumInliers, int degree, int secdegree, int label, int time) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(0), sublist.get(i).getDoublePosition(1) });
		}
		

		Pair<RegressionFunction, ArrayList<double[]>> resultcurvature = getLocalcurvature(Cordlist, centerpoint,
				smoothing, maxError, minNumInliers, degree, secdegree, label, time);

		// Draw the function

		return resultcurvature;

	}

	public Pair<Boolean, Double> FitonRightsubTree(InteractiveSimpleEllipseFit parent, RealLocalizable centerpoint,
			Node<RealLocalizable> leaf, ArrayList<double[]> interpolatedCurvature,
			ArrayList<RegressionFunction> functions, double smoothing, double maxError, int minNumInliers, int degree,
			int secdegree, int label, int time) {

		List<RealLocalizable> Rightsubtruths = leaf.rightTree;

		// Fit function on left tree

		ArrayList<double[]> RightCordlist = new ArrayList<double[]>();
		for (int i = 0; i < Rightsubtruths.size(); ++i) {

			RightCordlist.add(new double[] { Rightsubtruths.get(i).getDoublePosition(0),
					Rightsubtruths.get(i).getDoublePosition(1) });

		}
		Pair<RegressionFunction, ArrayList<double[]>> Rightresultcurvature = getLocalcurvature(RightCordlist,
				centerpoint, smoothing, maxError, minNumInliers, degree, secdegree, label, time);

		// Draw the function
		double perimeter = 0;
		if (Rightresultcurvature != null) {

			functions.add(Rightresultcurvature.getA());

			interpolatedCurvature.addAll(Rightresultcurvature.getB());

			perimeter = Rightresultcurvature.getA().Curvaturepoints.get(0)[3];

			boolean rightcorrected = CorrectSegmentRegression(Rightresultcurvature.getA());
			Pair<Boolean, Double> rightpair = new ValuePair<Boolean, Double>(rightcorrected, perimeter);
			return rightpair;
		}

		else
			return null;

	}

	public void MakeSegments(InteractiveSimpleEllipseFit parent, final List<RealLocalizable> truths, 
			int celllabel, int time) {


		double usersize = Math.round(parent.wavesize/ parent.calibration);
		
		
		
		if (usersize <= 1)
			usersize = 3;
		int segmentLabel = 1;

		Iterator<RealLocalizable> iterator = truths.iterator();

		List<RealLocalizable> sublist = new ArrayList<RealLocalizable>();

		int count = 0;

		while (iterator.hasNext()) {

			RealLocalizable current = iterator.next();

			sublist.add(current);

			count++;

			if (count >= usersize) {

				List<RealLocalizable> copyList = CopyList(sublist);

				parent.Listmap.put(segmentLabel, copyList);
				
				count = 0;
				segmentLabel++;
				sublist.clear();
			}

		}
		
		Iterator<RealLocalizable> iteratormore = truths.iterator();
		// If any points remain we take care of them by overlapping with the next region a bit
		if(sublist.size()!=0 && count > usersize / 2) {
			
			while(iteratormore.hasNext()) {
				
				RealLocalizable current = iteratormore.next();
				
				sublist.add(current);
				
				count++;
				
				if(count >=usersize) {
					
					List<RealLocalizable> copyList = CopyList(sublist);

					parent.Listmap.put(segmentLabel, copyList);
					
					break;
					
				}
				
			}
			
		}
		

	}

	public List<RealLocalizable> CopyList(List<RealLocalizable> truths) {

		List<RealLocalizable> sublist = new ArrayList<RealLocalizable>();
		Iterator<RealLocalizable> subiter = truths.iterator();

		while (subiter.hasNext()) {

			sublist.add(subiter.next());

		}

		return sublist;
	}

	public void MakeTree(InteractiveSimpleEllipseFit parent, final List<RealLocalizable> truths, int depthint,
			String depth, int maxdepth) {

		int size = truths.size();
		if (size <= 3)
			return;
		else {

			int splitindex;
			if (size % 2 == 0)
				splitindex = size / 2;
			else
				splitindex = (size - 1) / 2;

			final ArrayList<RealLocalizable> childA = new ArrayList<RealLocalizable>((int) size / 2);

			final ArrayList<RealLocalizable> childB = new ArrayList<RealLocalizable>((int) (size / 2 + size % 2));

			Iterator<RealLocalizable> iterator = truths.iterator();
			int index = 0;
			while (iterator.hasNext()) {

				iterator.next();

				if (index < splitindex)

					childA.add(truths.get(index));

				else

					childB.add(truths.get(index));

				index++;

			}

			Node<RealLocalizable> currentnode = new Node<RealLocalizable>(truths.get(splitindex), truths, childA,
					childB, depth);
			parent.Nodemap.put(depth, currentnode);

			depthint = depthint + 1;
			String depthleft = depth + Integer.toString(depthint) + "L";
			String depthright = depth + Integer.toString(depthint) + "R";
			MakeTree(parent, childA, depthint, depthleft, maxdepth);
			MakeTree(parent, childB, depthint, depthright, maxdepth);
		}

	}

	/**
	 * 
	 * Implementation of the curvature function to compute curvature at a point
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 * @return
	 */

	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, double smoothing, double maxError, int minNumInliers, int degree,
			int secdegree, int label, int time) {

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

		Pair<RegressionFunction, ArrayList<double[]>> finalfunctionandList;

		// Circle fits
		if (parent.circlefits) {
			finalfunctionandList = RansacEllipseBlock(list, centerpoint, 2);

		}
		// Polynomial fits
		else {

			finalfunctionandList = RansacBlock(pointlist, centerpoint, smoothing, maxError, minNumInliers, degree,
					secdegree);

		}
		return finalfunctionandList;

	}

	/**
	 * 
	 * Fit a quadratic function via regression (not recommended, use Ransac instead)
	 * 
	 * @param points
	 * @return
	 */
	public RegressionFunction RegressionBlock(ArrayList<Point> points, RealLocalizable center, int degree) {

		// DO not use this
		double[] x = new double[points.size()];
		double[] y = new double[points.size()];
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		for (int index = 0; index < points.size(); ++index) {

			x[index] = points.get(index).getW()[0];

			y[index] = points.get(index).getW()[1];

		}

		Threepointfit regression = null;

		if (points.size() > degree + 1) {

			regression = new Threepointfit(x, y, degree);

		}

		double perimeter = 0.5;
		double Kappa = 0;
		for (int index = 0; index < points.size() - 1; ++index) {

			double dx = Math.abs(points.get(index).getW()[0] - points.get(index + 1).getW()[0]);
			double firstderiv = regression.predictderivative(points.get(index).getW()[0]);

			perimeter += Math.sqrt(1 + firstderiv * firstderiv) * dx;

		}
		for (int index = 0; index < points.size() - 1; ++index) {

			double secderiv = regression.predictsecderivative(points.get(index).getW()[0]);
			double firstderiv = regression.predictderivative(points.get(index).getW()[0]);

			Kappa = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

			long[] posf = new long[] { (long) points.get(index).getW()[0], (long) points.get(index).getW()[1] };
			net.imglib2.Point point = new net.imglib2.Point(posf);
			Pair<Double, Double> Intensity = getIntensity(point, center);

			Curvaturepoints.add(new double[] { points.get(index).getW()[0], points.get(index).getW()[1],
					Math.abs(Kappa), perimeter, Kappa, Intensity.getA(), Intensity.getB() });

		}

		RegressionFunction finalfunction = new RegressionFunction(regression, Curvaturepoints);
		return finalfunction;

	}

	/**
	 * 
	 * Fit an ellipse to a bunch of points
	 * 
	 * @param pointlist
	 * @param ndims
	 * @return
	 */

	public Pair<RegressionFunction, ArrayList<double[]>> RansacEllipseBlock(final ArrayList<RealLocalizable> pointlist,
			RealLocalizable centerpoint, int ndims) {

		final RansacFunctionEllipsoid ellipsesegment = FitLocalEllipsoid.findLocalEllipsoid(pointlist, ndims);

		double Kappa = 0;
		double perimeter = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();

		double[] center = ellipsesegment.function.getCenter();
		double radii = ellipsesegment.function.getRadii();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];

		perimeter = Distance.DistanceSqrt(pointlist.get(0), pointlist.get(pointlist.size() - 1));
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

			Kappa = 1.0 / radii;
			for (int d = 0; d < newpos.length; ++d)
				longnewpos[d] = (long) newpos[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);
			Pair<Double, Double> Intensity = getIntensity(intpoint, centerpoint);
			
			// Average the intensity.
			meanIntensity += Intensity.getA();
			meanSecIntensity += Intensity.getB();
			AllCurvaturepoints.add(
					new double[] { newpos[0], newpos[1], Math.abs(Kappa), perimeter, meanIntensity, meanSecIntensity });
		}

		meanIntensity /= size;
		meanSecIntensity /= size;

		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.abs(Kappa), perimeter, meanIntensity, meanSecIntensity });

		
		RegressionFunction finalfunctionransac = new RegressionFunction(ellipsesegment.function, Curvaturepoints);

		return new ValuePair<RegressionFunction, ArrayList<double[]>>(finalfunctionransac, AllCurvaturepoints);
	}

	/**
	 * 
	 * Fitting a quadratic or a linear function using Ransac
	 * 
	 * @param pointlist
	 * @param maxError
	 * @param minNumInliers
	 * @param maxDist
	 * @return
	 */

	public Pair<RegressionFunction, ArrayList<double[]>> RansacBlock(final ArrayList<Point> pointlist,
			RealLocalizable center, double smoothing, double maxError, int minNumInliers, int degree, int secdegree) {

		// Ransac block
		MixedPolynomialFunction<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction, MixedPolynomial<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction>> mixedfunction = new MixedPolynomial<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction>(
				new HigherOrderPolynomialFunction(degree), new HigherOrderPolynomialFunction(secdegree), smoothing);
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		final RansacFunction segment = Tracking.findQuadLinearFunction(pointlist, mixedfunction, maxError,
				minNumInliers);

		if (segment != null) {
			double perimeter = 0.5;
			double Kappa = 0;

			for (int index = 0; index < segment.inliers.size() - 1; ++index) {

				PointFunctionMatch p = segment.inliers.get(index);
				PointFunctionMatch pnext = segment.inliers.get(index + 1);
				double dx = Math.abs(p.getP1().getW()[0] - pnext.getP1().getW()[0]);

				double firstderiv = segment.mixedfunction.getB().predictFirstderivative(p.getP1().getW()[0])
						* segment.mixedfunction.getLambda()
						+ segment.mixedfunction.getA().predictFirstderivative(p.getP1().getW()[0])
								* (1 - segment.mixedfunction.getLambda());

				perimeter += Math.sqrt(1 + firstderiv * firstderiv) * dx;

			}

			for (int index = 0; index < segment.inliers.size(); ++index) {
				PointFunctionMatch p = segment.inliers.get(index);
				double secderiv = segment.mixedfunction.getB().predictSecondderivative(p.getP1().getW()[0])
						* segment.mixedfunction.getLambda()
						+ segment.mixedfunction.getA().predictSecondderivative(p.getP1().getW()[0])
								* (1 - segment.mixedfunction.getLambda());
				double firstderiv = segment.mixedfunction.getB().predictFirstderivative(p.getP1().getW()[0])
						* segment.mixedfunction.getLambda()
						+ segment.mixedfunction.getA().predictFirstderivative(p.getP1().getW()[0])
								* (1 - segment.mixedfunction.getLambda());
				Kappa = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

				long[] posf = new long[] { (long) p.getP1().getW()[0], (long) p.getP1().getW()[1] };
				net.imglib2.Point point = new net.imglib2.Point(posf);
				Pair<Double, Double> Intensity = getIntensity(point, center);
				Curvaturepoints.add(new double[] { p.getP1().getW()[0], p.getP1().getW()[1], Math.abs(Kappa), perimeter,
						Kappa, Intensity.getA(), Intensity.getB() });

			}

			RegressionFunction finalfunctionransac = new RegressionFunction(segment.mixedfunction, Curvaturepoints,
					segment.inliers, segment.candidates);

			return new ValuePair<RegressionFunction, ArrayList<double[]>>(finalfunctionransac, Curvaturepoints);

		}

		else

			return null;

	}

	/**
	 * 
	 * Interpolate from (x, y) to (x, y) + 1 by filling up the values in between
	 * 
	 */

	public Pair<double[], double[]> InterpolateValues(final double[] Xcurr, final double[] Xnext,
			Threepointfit regression) {

		double minX = Xcurr[0] < Xnext[0] ? Xcurr[0] : Xnext[0];
		double maxX = Xcurr[0] > Xnext[0] ? Xcurr[0] : Xnext[0];

		double interpolant = 0.1;
		double X = minX;
		double Y = regression.predict(X);

		int steps = (int) ((maxX - minX) / interpolant);
		if (steps > 0) {
			double[] returnValX = new double[steps];
			double[] returnValY = new double[steps];

			returnValX[0] = X;
			returnValY[0] = Y;

			for (int i = 1; i < steps; ++i) {

				returnValX[i] = X + i * interpolant;
				returnValY[i] = regression.predict(returnValX[i]);

			}

			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(returnValX, returnValY);

			return interpolXY;
		}

		else {
			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(new double[] { X, Y },
					new double[] { X, Y });

			return interpolXY;

		}
	}

	/**
	 * 
	 * Evenly or unevenly spaced data derivative is computed via Lagrangian
	 * interpolation
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 */
	public double[] InterpolatedFirstderiv(double[] previousCord, double[] currentCord, double[] nextCord) {

		double y0 = previousCord[1];
		double y1 = currentCord[1];
		double y2 = nextCord[1];

		double x0 = previousCord[0];
		double x1 = currentCord[0];
		double x2 = nextCord[0];

		double x01 = x0 - x1;
		double x02 = x0 - x2;
		double x12 = x1 - x2;
		if (x01 != 0 && x02 != 0 && x12 != 0) {
			double diffatx0 = y0 * (x01 + x02) / (x01 * x02) - y1 * x02 / (x01 * x12) + y2 * x01 / (x02 * x12);
			double diffatx2 = -y0 * x12 / (x01 * x02) + y1 * x02 / (x01 * x12) - y2 * (x02 + x12) / (x02 * x12);
			double diffatx1 = y0 * (x12) / (x01 * x02) + y1 * (1.0 / x12 - 1.0 / x01) - y2 * x01 / (x02 * x12);

			double[] threepointdiff = { diffatx0, diffatx1, diffatx2 };

			return threepointdiff;
		} else

			return new double[] { 0, 0, 0 };

	}

	/**
	 * 
	 * Compute perimeter of a curve by adding up the distance between ordered set of
	 * points
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @return
	 */
	public double getPerimeter(List<RealLocalizable> orderedtruths, int ndims) {

		double perimeter = 0;
		for (int index = 1; index < orderedtruths.size(); ++index) {

			double[] lastpoint = new double[ndims];
			double[] currentpoint = new double[ndims];

			orderedtruths.get(index - 1).localize(lastpoint);
			orderedtruths.get(index).localize(currentpoint);
			perimeter += Distance.DistanceSq(lastpoint, currentpoint);

		}

		return perimeter;
	}

	/**
	 * Obtain intensity in the user defined
	 * 
	 * @param point
	 * @return
	 */

	public Pair<Double, Double> getIntensity(Localizable point, RealLocalizable center) {

		RandomAccess<FloatType> ranac = parent.CurrentViewOrig.randomAccess();

		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		ranac.setPosition(point);
		ranacsec.setPosition(ranac);
		double Intensity = ranac.get().getRealDouble();
		double IntensitySec = ranacsec.get().getRealDouble();
		
		
		double maxindistance;
		double maxoutdistance;
		if (parent.usedefaultrim) {

			maxindistance = 1;
			maxoutdistance = 1;
			return new ValuePair<Double, Double>(Intensity, IntensitySec);
			
		}

		else {
			maxindistance = parent.insidedistance;
			maxoutdistance = parent.outsidedistance;

		

		double fcteps = 1.0E-30;
		long step = point.getLongPosition(0) - (long) center.getFloatPosition(0);

		int signum;
		if (step < 0)
			signum = 1;
		else
			signum = -1;

		long movestep = 1;
		long slope = (long) ((point.getLongPosition(1) - (long) center.getFloatPosition(1)) / (step + fcteps));
		long intercept = point.getLongPosition(1) - slope * point.getLongPosition(0);
		

		int i = 0;

		do {
			long x = ranac.getLongPosition(0) + i * movestep * signum;
			long y = slope * ranac.getLongPosition(0) + intercept;
			ranac.setPosition(new long[] { x, y });
			ranacsec.setPosition(ranac);
			i++;
			Intensity += ranac.get().getRealDouble();
			IntensitySec += ranacsec.get().getRealDouble();

			if (Distance.DistanceSq(new double[] { point.getDoublePosition(0), point.getDoublePosition(1) },
					new double[] { x, y }) <= maxindistance * maxindistance)
				break;
		} while (true);

		i = 0;

		do {
			long x = ranac.getLongPosition(0) - i * movestep * signum;
			long y = slope * ranac.getLongPosition(0) + intercept;
			ranac.setPosition(new long[] { x, y });
			ranacsec.setPosition(ranac);
			i++;
			Intensity += ranac.get().getRealDouble();
			IntensitySec += ranacsec.get().getRealDouble();
			if (Distance.DistanceSq(new double[] { point.getDoublePosition(0), point.getDoublePosition(1) },
					new double[] { x, y }) <= maxoutdistance * maxoutdistance)
				break;
		} while (true);

		return new ValuePair<Double, Double>(Intensity, IntensitySec);
		}
	}

}
