package curvatureFinder;

import java.util.ArrayList;

import ransacPoly.RegressionFunction;

public class RegressionLineProfile {

	public final RegressionFunction regfunc;
	
	public final ArrayList<LineProfileCircle> LineScanIntensity;
	
	public final ArrayList<double[]> AllCurvaturepoints;
	
	
	
	public RegressionLineProfile(final RegressionFunction regfunc, final ArrayList<LineProfileCircle> LineScanIntensity, final ArrayList<double[]> AllCurvaturepoints ) {
		
		
		this.regfunc = regfunc;
		
		this.LineScanIntensity = LineScanIntensity;
		
		this.AllCurvaturepoints = AllCurvaturepoints;
		
		
		
	}
	
	
	public RegressionLineProfile(final RegressionFunction regfunc, final ArrayList<double[]> AllCurvaturepoints ) {
		
		
		this.regfunc = regfunc;
		
		this.LineScanIntensity = null;
		
		this.AllCurvaturepoints = AllCurvaturepoints;
		
		
		
	}
	
	
	
}
