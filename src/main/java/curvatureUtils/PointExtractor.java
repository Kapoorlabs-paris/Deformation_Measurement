package curvatureUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ellipsoidDetector.Intersectionobject;
import ij.gui.Line;
import ij.gui.OvalRoi;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import ransacPoly.QuadraticFunction;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.DisplayAuto;

public class PointExtractor {

	/**
	 * The functions can come from either regression or from Ransac, this routine converts the curvature object to intersection object
	 * 
	 * @param localCurvature
	 * @param functions
	 * @return
	 */
	public static Intersectionobject CurvaturetoIntersection(final ArrayList<Curvatureobject> localCurvature, final ArrayList<RegressionFunction> functions) {

		ArrayList<Line> resultlineroi = new ArrayList<Line>();
		ArrayList<double[]> linelist = new ArrayList<double[]>();
		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		int celllabel, t, z;
		double perimeter;
		celllabel = localCurvature.get(0).Label;
        t = localCurvature.get(0).t;
        z = localCurvature.get(0).z;
		perimeter = localCurvature.get(0).perimeter;
		ArrayList<OvalRoi> resultcurveline = new ArrayList<OvalRoi>();
		ArrayList<OvalRoi> resultallcurveline = new ArrayList<OvalRoi>();
		for (int i = 0; i < functions.size(); ++i) {
		
			RegressionFunction regression = functions.get(i);
		
		for (int index = 0; index < regression.Curvaturepoints.size() - 1; ++index) {
			int xs = (int) regression.Curvaturepoints.get(index)[0];
			int xe = (int) regression.Curvaturepoints.get(index + 1)[0];
			
			int ys = 0;
			int ye = 0;
			// If the method of fitting a function was regression
			if(regression.regression!=null) {
				
			
			ys = (int)regression.regression.predict(xs);
			ye = (int)regression.regression.predict(xe);
			
			
			}
			// If the method of fitting a function was Ransac
			else {
				
				ys = (int)((QuadraticFunction) regression.quad).predict(xs);
				ye = (int)((QuadraticFunction) regression.quad).predict(xe);
				
			}
			
			// Store the information to draw a line as a lineroi
			Line line = new Line(xs, ys, xe, ye);
			resultlineroi.add(line);
		
		}
					
			if (regression.inliers!=null) {
			resultcurveline.addAll(DisplayAuto.DisplayInliers(regression.inliers));
			resultallcurveline.addAll(DisplayAuto.DisplayInliers(regression.candidates));
		}
			else {
				
				resultallcurveline.addAll(DisplayAuto.DisplayPointInliers(regression.Curvaturepoints));
			}
			
			
		}


        for (int index = 0; index < localCurvature.size() - 1; ++index) {


			X[index] = localCurvature.get(index).cord[0];
			Y[index] = localCurvature.get(index).cord[1];
			Z[index] = localCurvature.get(index).radiusCurvature;
			// Make the line list for making intersection object
			linelist.add(new double[] {X[index], Y[index], Z[index]});
			
	

		}
		
        // Compute the geometric mean of the object, which we would need for tracking
		double[] mean = GeometricCenter(X, Y);
		
		
		Intersectionobject currentIntersection = new Intersectionobject(mean, linelist, resultlineroi, resultcurveline, resultallcurveline, perimeter, celllabel, t, z);

		return currentIntersection;
		
	}
	
	/**
	 * Take mean of X, Y set of values
	 * 
	 * @param X
	 * @param Y
	 * @return
	 */
	
	public static double[] GeometricCenter(double[] X, double[] Y) {
		
		int length = X.length;
		double Xmean = 0;
		double Ymean = 0;
		
	  for (int i = 0; i < length; ++i) {
		
		  Xmean+=X[i];
	      Ymean+=Y[i];
		
	  }
	  
	  double[] mean = new double[] {Xmean / length, Ymean / length};
	  
	  return mean;
	  
	}
	
	
}
