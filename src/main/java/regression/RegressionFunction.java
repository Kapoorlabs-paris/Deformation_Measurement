package regression;

import java.util.ArrayList;

public class RegressionFunction {

	
	public final Threepointfit regression;
	
	public final ArrayList<double[]> Curvaturepoints;
	
	public final double perimeter;
	
	
	public RegressionFunction (final Threepointfit regression, final ArrayList<double[]> Curvaturepoints, final double perimeter) {
		
		
		this.regression = regression;
		
		this.Curvaturepoints = Curvaturepoints;
		
		this.perimeter = perimeter;
		
	}
	
	
	
}
