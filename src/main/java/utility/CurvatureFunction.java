package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import curvatureUtils.Node;
import drawUtils.DrawFunction;
import ellipsoidDetector.Distance;
import ij.ImagePlus;
import mpicbg.models.Point;
import net.imglib2.RealLocalizable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import ransac.PointFunctionMatch.PointFunctionMatch;
import ransac.loadFiles.Tracking;
import ransacPoly.AbstractFunction2D;
import ransacPoly.LinearFunction;
import ransacPoly.QuadraticFunction;
import ransacPoly.RansacFunction;
import ransacPoly.RegressionFunction;
import ransacPoly.Sort;
import ransacPoly.Threepointfit;

public class CurvatureFunction {

	static int evendepth;

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
	public static ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> getCurvature(
			InteractiveSimpleEllipseFit parent, List<RealLocalizable> truths, double maxError, int minNumInliers,
			double maxDist, int ndims, int Label, int t, int z) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		ArrayList<double[]> interpolatedCurvature = new ArrayList<double[]>();
		ArrayList<RegressionFunction> functions = new ArrayList<RegressionFunction>();

		
		double perimeter = 0;
		// Extract the depth of the tree from the user input

		// Extract the last split left and right tree

		for (int index = 0; index < parent.Allnodes.size(); ++index) {

			Node<RealLocalizable> node = parent.Allnodes.get(index);
			
		System.out.println(node.depth + " " + evendepth);
			if (node.depth == evendepth) {

				System.out.println(node.depth + " " + node.parent.size() + " " + "Size");
				// Get the left and right tree of the node and do ransac fits

				// Output is the local perimeter of the fitted function
				double perimeterlocal = FitonsubTree(parent, node, interpolatedCurvature, functions, maxError,
						minNumInliers, maxDist);


				// Add local perimeters to get total perimeter of the curve
				perimeter += perimeterlocal;

				
			}

		
		}

		for (int indexx = 0; indexx < interpolatedCurvature.size(); ++indexx) {

			Curvatureobject currentobject = new Curvatureobject(interpolatedCurvature.get(indexx)[2], perimeter, Label,
					new double[] { interpolatedCurvature.get(indexx)[0], interpolatedCurvature.get(indexx)[1] }, t, z);

			curveobject.add(currentobject);

			System.out.println("Kappa" + Math.abs(interpolatedCurvature.get(indexx)[2]) + " " + perimeter + " "
					+ interpolatedCurvature.get(indexx)[0] + " " + interpolatedCurvature.get(indexx)[1]);
		}
		
	

		return new ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>(functions, curveobject);

	}
	
	
	public static int Getdepth(InteractiveSimpleEllipseFit parent) {
		
		
		double nearestsize = Double.MAX_VALUE;
		int depth = 0;
		for (Node<RealLocalizable> node : parent.Allnodes) {
			
			int currentsize = node.parent.size();
			
			if (Math.abs(currentsize - parent.minNumInliers) <= nearestsize) {
				
				
				nearestsize = currentsize;
				
				depth = node.depth;
			}
			
		}
		
		return depth;
	}

	public static double FitonsubTree(InteractiveSimpleEllipseFit parent, Node<RealLocalizable> leaf,
			ArrayList<double[]> interpolatedCurvature, ArrayList<RegressionFunction> functions, double maxError,
			int minNumInliers, double maxDist) {

		List<RealLocalizable> Leftsubtruths = leaf.parent;

		// Fit function on left tree

		ArrayList<double[]> LeftCordlist = new ArrayList<double[]>();
		for (int i = 0; i < Leftsubtruths.size(); ++i) {

			LeftCordlist.add(new double[] { Leftsubtruths.get(i).getDoublePosition(0),
					Leftsubtruths.get(i).getDoublePosition(1) });

		}
		RegressionFunction Leftresultcurvature = getLocalcurvature(LeftCordlist, maxError, minNumInliers, maxDist);

		if(Leftresultcurvature !=null) {
		// Draw the function

		functions.add(Leftresultcurvature);

		interpolatedCurvature.addAll(Leftresultcurvature.Curvaturepoints);

		double perimeter = Leftresultcurvature.Curvaturepoints.get(0)[3];

		return perimeter;
		}
		
		else return 0;

	}

	public static void MakeTree(InteractiveSimpleEllipseFit parent, final List<RealLocalizable> truths, int depth, int maxdepth) {

		
		
		if (truths.size() <= 3 || depth > maxdepth)
			return;
		
	

		else  {
			
			evendepth = depth;
			System.out.println(truths.size() + " " + depth + "Depth" +  " " + maxdepth + " " + evendepth);
			int size = truths.size();

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

			parent.Allnodes.add(currentnode);

			MakeTree(parent, childA, depth + 1, maxdepth);
			MakeTree(parent, childB, depth + 1, maxdepth);

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

	public static RegressionFunction getLocalcurvature(ArrayList<double[]> Cordlist, double maxError, int minNumInliers,
			double maxDist) {

		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();

		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			pointlist.add(new Point(new double[] { x[index], y[index] }));

		}

		// Use Ransac to fit a quadratic or a linear function

		RegressionFunction finalfunction = RansacBlock(pointlist, maxError, minNumInliers, maxDist);

		if(finalfunction!=null)
	
		return finalfunction;
		
		else return null;
	}

	/**
	 * 
	 * Fit a quadratic function via regression (not recommended, use Ransac instead)
	 * 
	 * @param points
	 * @return
	 */
	public static RegressionFunction RegressionBlock(ArrayList<double[]> points) {

		double[] x = new double[points.size()];
		double[] y = new double[points.size()];
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		for (int index = 0; index < points.size(); ++index) {

			x[index] = points.get(index)[0];

			y[index] = points.get(index)[1];

		}

		Threepointfit regression = new Threepointfit(x, y, 2);

		double highestCoeff = regression.GetCoefficients(2);
		double sechighestCoeff = regression.GetCoefficients(1);

		double perimeter = 0;
		double Kappa = 0;
		for (int index = 0; index < points.size() - 1; ++index) {

			double dx = Math.abs(points.get(index)[0] - points.get(index + 1)[0]);
			double secderiv = 2 * highestCoeff;
			double firstderiv = 2 * highestCoeff * points.get(index)[0] + sechighestCoeff;

			Kappa += secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

			perimeter += Math.sqrt(1 + firstderiv * firstderiv) * dx;

		}

		for (int index = 0; index < points.size() - 1; ++index) {
			if (perimeter > 0)
				Curvaturepoints.add(new double[] { points.get(index)[0], points.get(index)[1],
						Math.abs(Kappa) / perimeter, perimeter });
		}
		RegressionFunction finalfunction = new RegressionFunction(regression, Curvaturepoints);
		return finalfunction;

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

	public static RegressionFunction RansacBlock(final ArrayList<Point> pointlist, double maxError, int minNumInliers,
			double maxDist) {

		// Ransac block
		QuadraticFunction function = new QuadraticFunction();

		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		final RansacFunction segment = Tracking.findQuadLinearFunction(pointlist, function, maxError, minNumInliers,
				maxDist);
		
		if (segment!=null) {
		double perimeter = 0;
		double Kappa = 0;

		double highestCoeff = segment.function.getCoefficient(2);
		double sechighestCoeff = segment.function.getCoefficient(1);

		for (int index = 0; index < segment.inliers.size() - 1; ++index) {

			PointFunctionMatch p = segment.inliers.get(index);
			PointFunctionMatch pnext = segment.inliers.get(index + 1);

			double dx = Math.abs(p.getP1().getW()[0] - pnext.getP1().getW()[0]);
			double secderiv = 2 * highestCoeff;
			double firstderiv = 2 * highestCoeff * p.getP1().getW()[0] + sechighestCoeff;
			Kappa += secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);
			perimeter += Math.sqrt(1 + firstderiv * firstderiv) * dx;

		}
		for (int index = 0; index < segment.inliers.size() - 1; ++index) {
			PointFunctionMatch p = segment.inliers.get(index);
			if (perimeter > 0)
				Curvaturepoints.add(new double[] { p.getP1().getW()[0], p.getP1().getW()[1],
						Math.abs(Kappa) / perimeter, perimeter });
		}
		RegressionFunction finalfunction = new RegressionFunction(segment.function, Curvaturepoints, segment.inliers);
		return finalfunction;
		}
		
		else
			
			return null;
	}

	/**
	 * 
	 * Interpolate from (x, y) to (x, y) + 1 by filling up the values in between
	 * 
	 */

	public static Pair<double[], double[]> InterpolateValues(final double[] Xcurr, final double[] Xnext,
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
	public static double[] InterpolatedFirstderiv(double[] previousCord, double[] currentCord, double[] nextCord) {

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
	public static double getPerimeter(List<RealLocalizable> orderedtruths, int ndims) {

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

}
