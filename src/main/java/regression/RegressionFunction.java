package regression;

import java.util.ArrayList;

import ransacPoly.QuadraticFunction;

public class RegressionFunction {

	
	public final Threepointfit regression;
	
	public final QuadraticFunction quad;
	
	public final ArrayList<double[]> Curvaturepoints;
	
	public final double perimeter;
	
	
	public RegressionFunction (final Threepointfit regression, final ArrayList<double[]> Curvaturepoints, final double perimeter) {
		
		
		this.regression = regression;
		
		this.quad = null;
		
		this.Curvaturepoints = Curvaturepoints;
		
		this.perimeter = perimeter;
		
	}
	
    public RegressionFunction (final QuadraticFunction quad, final ArrayList<double[]> Curvaturepoints, final double perimeter) {
		
		
		this.regression = null;
		
		this.quad = quad;
		
		this.Curvaturepoints = Curvaturepoints;
		
		this.perimeter = perimeter;
		
	}
	
}
