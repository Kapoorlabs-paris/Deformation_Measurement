package pluginTools;

import java.util.ArrayList;

import kalmanForSegments.Segmentobject;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;

public class RegressionCurveSegment {
	
	
	
	public final ArrayList<RegressionFunction> functionlist;
	public final ArrayList<Curvatureobject> Curvelist;
	
	public RegressionCurveSegment(final ArrayList<RegressionFunction> functionlist, final ArrayList<Curvatureobject> Curvelist) {
		
		
		this.functionlist = functionlist;
		
		this.Curvelist = Curvelist;
		
		
		
	}
	

}
