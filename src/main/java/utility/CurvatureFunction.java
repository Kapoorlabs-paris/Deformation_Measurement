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
		
		for (int index = 1; index < orderedtruths.size() - 1; index+=3) {
			double[] lastpoint = new double[ndims];
			double[] currentpoint = new double[ndims];
			double[] nextpoint = new double[ndims];
			orderedtruths.get(index - 1).localize(lastpoint);
			orderedtruths.get(index).localize(currentpoint);
			orderedtruths.get(index + 1).localize(nextpoint);
			
			double[] radiusCurvature = getLocalcurvature(lastpoint, currentpoint, nextpoint);
			
			Curvatureobject previousobject = new Curvatureobject(radiusCurvature[0], perimeter, Label, lastpoint, t, z);
			Curvatureobject currentobject = new Curvatureobject(radiusCurvature[1], perimeter, Label, currentpoint, t, z);
			Curvatureobject nextobject = new Curvatureobject(radiusCurvature[2], perimeter, Label, nextpoint, t, z);
			
			curveobject.add(previousobject);
			curveobject.add(currentobject);
			curveobject.add(nextobject);
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
	
	public static double[] getLocalcurvature(double[] previousCord, double[] currentCord, double[] nextCord) {
		
		
		double[] firstderiv = InterpolatedFirstderiv(previousCord, currentCord, nextCord);
		
		double[] secpreviousCord = {previousCord[0], firstderiv[0]};
		double[] seccurrentCord = {currentCord[0], firstderiv[1]};
		double[] secnextCord = {nextCord[0], firstderiv[2]};
		
		double[] secderiv = InterpolatedFirstderiv(secpreviousCord, seccurrentCord, secnextCord);
		
	    
        double[] signedcurvature = new double[3];
        		
     signedcurvature[0]  =  secderiv[0] / Math.pow((1 + firstderiv[0] * firstderiv[0]), 3.0/2.0);
     signedcurvature[1]  =  secderiv[1] / Math.pow((1 + firstderiv[1] * firstderiv[1]), 3.0/2.0);
     signedcurvature[2]  =  secderiv[2] / Math.pow((1 + firstderiv[2] * firstderiv[2]), 3.0/2.0);
        
		return signedcurvature;
		}

	
	
	
	/**
	 * 
	 * Evenly or unevenly spaced data derivative is computed via Lagrangian interpolation
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
		if(x01!= 0 && x02!= 0&& x12!= 0) {
		double diffatx0 = y0 * (x01 + x02)/(x01 * x02) - y1 * x02/(x01 * x12) + y2 * x01/(x02 * x12);
		double diffatx2 = -y0 * x12/(x01 * x02) + y1 * x02/(x01 * x12) - y2 * (x02 + x12)/(x02 * x12);
		double diffatx1 = y0 * (2*x1 - x1 - x2)/(x01 * x02) - y1 *(2*x1 - x0 - x2)/(x01 * x12) + y2 * (2 * x1 - x0 - x1)/(x02*x12);
		
		double[] threepointdiff = {diffatx0, diffatx1, diffatx2};
		
		return threepointdiff;
		}
		else
			
			return new double[] {0,0,0};
		
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
