package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ellipsoidDetector.Distance;
import mpicbg.models.Point;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import ransac.PointFunctionMatch.PointFunctionMatch;
import ransac.loadFiles.Tracking;
import ransacPoly.AbstractFunction2D;
import ransacPoly.QuadraticFunction;
import ransacPoly.Sort;
import regression.Threepointfit;

public class CurvatureFunction {

	/**
	 * 
	 * Take in a list of ordered co-ordinates and compute a curvature object
	 * containing the curvature information at each co-ordinate
	 * 
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @return
	 */
	public static ArrayList<Curvatureobject> getCurvature(List<RealLocalizable> truths, int minInliers, int numseg, int ndims,
			int Label, int t, int z) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		ArrayList<double[]> interpolatedCurvature = new ArrayList<double[]>();

		double perimeter = getPerimeter(truths, ndims);
		int actualnumseg = numseg;
		int blocksize = (int) (truths.size() / actualnumseg);

		

		int sublist = 0;
		
	
			do {
				if((truths.size() % actualnumseg) == 0) {
			List<RealLocalizable> subtruths = truths.subList(sublist, sublist + blocksize);
			
			ArrayList<double[]> Cordlist = new ArrayList<double[]>();
			for (int i = 0; i < subtruths.size(); ++i) {

				Cordlist.add(new double[] { subtruths.get(i).getDoublePosition(0),
						subtruths.get(i).getDoublePosition(1) });

			}
			ArrayList<double[]> resultcurvature = getLocalcurvature(Cordlist, minInliers);
            interpolatedCurvature.addAll(resultcurvature);
			
			sublist += blocksize;
				}
				else {
					truths.remove(truths.size() - 1);
					blocksize = (int) (truths.size() / actualnumseg);
					
				}
				
			}while(sublist + blocksize <= truths.size());
		for (int indexx = 0; indexx < interpolatedCurvature.size(); ++indexx) {
			Curvatureobject currentobject = new Curvatureobject(interpolatedCurvature.get(indexx)[2], perimeter,
					Label, new double[] {interpolatedCurvature.get(indexx)[0], interpolatedCurvature.get(indexx)[1]}, t, z);

			curveobject.add(currentobject);
		}

		return curveobject;

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

	public static ArrayList<double[]> getLocalcurvature(ArrayList<double[]> Cordlist, int minInliers) {

		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<double[]> InterpolCordlist = new ArrayList<double[]>();
		ArrayList<Point> points = new ArrayList< Point >();
		ArrayList<double[]> Curvaturepoints = new ArrayList< double[] >();
		
		
		AbstractFunction2D function  = new QuadraticFunction();
		for (int index = 0; index < Cordlist.size(); ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];
			points.add( new Point( new double[]{ x[index], y[index] } ) );
		}

		
		final Pair<QuadraticFunction, ArrayList<PointFunctionMatch>> foundfunction = Tracking
				.findFunction(points, function, 0.75, minInliers , 1.1);
	
		
		double highestCoeff = foundfunction.getA().getCoefficient(2);
		double sechighestCoeff = foundfunction.getA().getCoefficient(1);
		double lowestCoeff = foundfunction.getA().getCoefficient(0);
		Iterator<Point> iter = points.iterator();
		
		
		while(iter.hasNext()) {
			
			
			Point currentpoint = iter.next();
			
			double secderiv = 2 * highestCoeff;
			double firstderiv = 2 * highestCoeff * currentpoint.getL()[0] + sechighestCoeff;
			double Kappa = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);
			
			
			Curvaturepoints.add(new double[] {currentpoint.getL()[0], currentpoint.getL()[1], Kappa});
			
		}
		
	
		return Curvaturepoints;
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
		if(steps > 0) {
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
			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(new double[] {X, Y}, new double[] {X, Y});
			
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
