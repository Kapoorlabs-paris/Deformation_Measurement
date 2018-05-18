package utility;

import java.util.ArrayList;
import java.util.List;

import ellipsoidDetector.Distance;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
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
	public static ArrayList<Curvatureobject> getCurvature(List<RealLocalizable> orderedtruths, int ndims, int Label,
			int t, int z) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		ArrayList<Pair<double[], Double>> interpolatedCurvature = new ArrayList<Pair<double[], Double>>();
		double perimeter = getPerimeter(orderedtruths, ndims);

		for (int i = 1; i < orderedtruths.size() - 1; ++i) {
			double[] lastpoint = new double[ndims];
			double[] currentpoint = new double[ndims];
			double[] nextpoint = new double[ndims];
			orderedtruths.get(i - 1).localize(lastpoint);
			orderedtruths.get(i).localize(currentpoint);
			orderedtruths.get(i + 1).localize(nextpoint);
		interpolatedCurvature.addAll(getLocalcurvature(lastpoint, currentpoint, nextpoint));

		
	
			
		}
		
		for (int indexx = 0; indexx < interpolatedCurvature.size(); ++indexx) {
			
			Curvatureobject currentobject = new Curvatureobject(interpolatedCurvature.get(indexx).getB(), perimeter, Label,
					interpolatedCurvature.get(indexx).getA(), t, z);
			System.out.println(interpolatedCurvature.get(indexx).getB());
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

	public static ArrayList<Pair<double[], Double>> getLocalcurvature(double[] previousCord, double[] currentCord,
			double[] nextCord) {

		double[] x = new double[3];
		double[] y = new double[3];

		x[0] = previousCord[0];
		x[1] = currentCord[0];
		x[2] = nextCord[0];

		y[0] = previousCord[1];
		y[1] = currentCord[1];
		y[2] = nextCord[1];
		ArrayList<Pair<double[], Double>> interpolatedCurvature = new ArrayList<Pair<double[], Double>>();

		Threepointfit regression = new Threepointfit(x, y, 2);

		for (int t = 0; t < x.length; ++t) {

			double[] cord = { x[t] , y[t] };

			double secderiv = 2 *  regression.GetCoefficients(2);
			double firstderiv = 2 *  regression.GetCoefficients(2) * x[t] + regression.GetCoefficients(1);
			double poly = secderiv
					/ Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

			Pair<double[], Double> finalresult = new ValuePair<double[], Double>(cord, Math.abs(poly));

			interpolatedCurvature.add(finalresult);
		}

		return interpolatedCurvature;
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
