package utility;

import java.util.ArrayList;
import java.util.List;

import ellipsoidDetector.Distance;
import net.imglib2.RealLocalizable;

public class CurvatureFunction {
	
	/**
	 * 
	 * Take in a  list of ordered co-ordinates and compute a curvature object containing the curvature information at each co-ordinate
	 * 
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @return
	 */
	public static ArrayList<Curvatureobject> getCurvature(List<RealLocalizable> orderedtruths, int ndims, int Label, int t, int z) {
		
		
		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();
		
		double perimeter = getPerimeter(orderedtruths, ndims);
		
		for (int index = 1; index < orderedtruths.size() - 1; ++index) {
			double[] lastpoint = new double[ndims];
			double[] currentpoint = new double[ndims];
			double[] nextpoint = new double[ndims];
			orderedtruths.get(index - 1).localize(lastpoint);
			orderedtruths.get(index).localize(currentpoint);
			orderedtruths.get(index + 1).localize(nextpoint);
			
			double radiusCurvature = getLocalcurvature(lastpoint, currentpoint, nextpoint);
			
			if(radiusCurvature!=Double.NaN) {
			Curvatureobject currentobject = new Curvatureobject(radiusCurvature, perimeter, Label, currentpoint, t, z);
			curveobject.add(currentobject);
			}
		}
		
		return curveobject;
		
	}
	
	/**
	 * 
	 * Implementation of the curvatrue function to compute curvatrue at a point
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 * @return
	 */
	
	public static double getLocalcurvature(double[] previousCord, double[] currentCord, double[] nextCord) {
		
		
		double deltax = currentCord[0] - previousCord[0];
		if (deltax == 0) {
			return 0;
			
		}
		
		else {
		double secderiv = (nextCord[1] - 2 * currentCord[1] + previousCord[1]) / (deltax * deltax);
		double firstderiv = (currentCord[1] - previousCord[1]) / deltax;
              
        double signedcurvature = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0/2.0);
 
        
		return signedcurvature;
		}
	}
	
	/**
	 * 
	 * Compute perimeter of a curve by adding up the distance between ordered set of points
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
			perimeter+=Distance.DistanceSq(lastpoint, currentpoint);
			
		}
		
		return perimeter;
	}

}
