package curvatureUtils;

import java.util.ArrayList;
import java.util.List;

import ellipsoidDetector.Intersectionobject;
import ij.gui.Line;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.Curvatureobject;

public class PointExtractor {

	
	/**
	 * 
	 * Use the information stored in curvature object to make an intersection object for tracking
	 * 
	 * @param localCurvature
	 */
	public static Intersectionobject CurvaturetoIntersection(final ArrayList<Curvatureobject> localCurvature) {

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
        for (int index = 0; index < localCurvature.size() - 1; ++index) {

			Curvatureobject currentcurvature = localCurvature.get(index);
			Curvatureobject currentcurvaturenext = localCurvature.get(index + 1);

			X[index] = localCurvature.get(index).cord[0];
			Y[index] = localCurvature.get(index).cord[1];
			Z[index] = localCurvature.get(index).radiusCurvature;
			// Make the line list for making intersection object
			linelist.add(new double[] {X[index], Y[index], Z[index]});
			
			// Make the line roi for intersection object
			Line currentline = new Line(currentcurvature.cord[0], currentcurvature.cord[1],
					currentcurvaturenext.cord[0], currentcurvaturenext.cord[1]);
			resultlineroi.add(currentline);

		}
		
		double[] mean = GeometricCenter(X, Y);
		
		Intersectionobject currentIntersection = new Intersectionobject(mean, linelist, resultlineroi, perimeter, celllabel, t, z);

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
