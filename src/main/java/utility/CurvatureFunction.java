package utility;

import java.util.ArrayList;
import java.util.Iterator;
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
	public static ArrayList<Curvatureobject> getCurvature(List<RealLocalizable> truths, int numseg, int ndims,
			int Label, int t, int z) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		ArrayList<Pair<double[], Double>> interpolatedCurvature = new ArrayList<Pair<double[], Double>>();

		double perimeter = getPerimeter(truths, ndims);
		int actualnumseg = numseg;
		int blocksize = (int) (truths.size() / actualnumseg);

		

		int sublist = 0;
		
	
			do {
				System.out.println(sublist + " sss" + blocksize + " " + actualnumseg);
				if((truths.size() % actualnumseg) == 0) {
			List<RealLocalizable> subtruths = truths.subList(sublist, sublist + blocksize);
			System.out.println(subtruths.size());
			
			ArrayList<double[]> Cordlist = new ArrayList<double[]>();
			for (int i = 0; i < subtruths.size(); ++i) {

				Cordlist.add(new double[] { subtruths.get(i).getDoublePosition(0),
						subtruths.get(i).getDoublePosition(1) });

			}
			Pair<ArrayList<double[]>, Double> resultcurvature = getLocalcurvature(Cordlist);

			for (int i = 0; i < Cordlist.size(); ++i) {
				Pair<double[], Double> interpolresult = new ValuePair<double[], Double>(Cordlist.get(i),
						resultcurvature.getB());
				interpolatedCurvature.add(interpolresult);
				
			}
			sublist += blocksize;
				}
				else {
					actualnumseg--;
					blocksize = (int) (truths.size() / actualnumseg);
					
				}
				
			}while(sublist + blocksize <= truths.size());
		for (int indexx = 0; indexx < interpolatedCurvature.size(); ++indexx) {
			Curvatureobject currentobject = new Curvatureobject(interpolatedCurvature.get(indexx).getB(), perimeter,
					Label, interpolatedCurvature.get(indexx).getA(), t, z);

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

	public static Pair<ArrayList<double[]>, Double> getLocalcurvature(ArrayList<double[]> Cordlist) {

		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<double[]> InterpolCordlist = new ArrayList<double[]>();

		for (int index = 0; index < Cordlist.size(); ++index) {

			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];
		}

		Threepointfit regression = new Threepointfit(x, y, 2);

		for (int index = 0; index < Cordlist.size() - 1; ++index) {

			double[] Xcurr = new double[] { Cordlist.get(index)[0], Cordlist.get(index)[1] };
			double[] Xnext = new double[] { Cordlist.get(index + 1)[0], Cordlist.get(index + 1)[1] };

			Pair<double[], double[]> interpolXY = InterpolateValues(Xcurr, Xnext, regression);

			double[] Xinterpol = interpolXY.getA();
			double[] Yinterpol = interpolXY.getB();

			for (int i = 0; i < Xinterpol.length; ++i) {
				InterpolCordlist.add(new double[] { Xinterpol[i], Yinterpol[i] });
			}
		}

		double averagecurvature = 0;
		for (int t = 0; t < x.length; ++t) {

			double secderiv = 2 * regression.GetCoefficients(2);
			double firstderiv = 2 * regression.GetCoefficients(2) * x[t] + regression.GetCoefficients(1);
			double poly = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

			averagecurvature += Math.abs(poly);
		}

		averagecurvature /= x.length;
System.out.println(averagecurvature);
		Pair<ArrayList<double[]>, Double> resultcurvature = new ValuePair<ArrayList<double[]>, Double>(InterpolCordlist,
				averagecurvature);
		return resultcurvature;
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
