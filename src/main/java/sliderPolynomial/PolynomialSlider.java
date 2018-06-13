package sliderPolynomial;

import java.util.ArrayList;

import curvatureUtils.Node;
import net.imglib2.RealLocalizable;
import ransacPoly.RegressionFunction;
import utility.CurvatureFunction;

public class PolynomialSlider {
	
	
	
	public static RegressionFunction DegreeCorrection(RegressionFunction Result, double smoothing, double maxError, double threshold,
			int minNumInliers, int degree, int secdegree) {
		
		
		
		ArrayList<double[]> Cordlist = Result.Curvaturepoints;
		RegressionFunction corrected;
		
		
			// We need to correct
			
			System.out.println("Correcting with higher degree" + degree + " " + secdegree);
			// Try increasing the polynomial fit degree
			corrected = CurvatureFunction.getLocalcurvature(Cordlist, smoothing, maxError, minNumInliers,
					degree, secdegree);
			
			int inliersize = corrected.inliers.size();
			int totalsize = corrected.candidates.size();
			double ratio = inliersize / totalsize;
			
			
			if(ratio >= threshold)
			return corrected;
			else {
				
				System.out.println("Increasing degree failed");
				return Result;
			}
			
		
		
	}
	

	public static RegressionFunction SizeCorrection(Node<RealLocalizable> leaf, RegressionFunction Result, double smoothing, double maxError, double threshold,
			int minNumInliers, int degree, int secdegree) {
		
		
		
		ArrayList<double[]> Cordlist = Result.Curvaturepoints;
		RegressionFunction corrected;
		
		
			// We need to correct
			degree++;
			secdegree++;
			System.out.println("Correcting by decreasing size" + leaf.leftTree.size() + " " + leaf.rightTree.size());
			// Try increasing the polynomial fit degree
			corrected = CurvatureFunction.getLocalcurvature(Cordlist, smoothing, maxError, minNumInliers,
					degree, secdegree);
			
			int inliersize = corrected.inliers.size();
			int totalsize = corrected.candidates.size();
			double ratio = inliersize / totalsize;
			
			
			if(ratio >= threshold)
			return corrected;
			else {
				
				System.out.println("Increasing degree failed");
				return null;
			}
			
		
		
	}

}
