package pluginTools;

import java.util.ArrayList;

import kalmanForSegments.Segmentobject;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;

public class RegressionCurveSegment {
	
	
	
	public final ArrayList<RegressionFunction> functionlist;
	public final ArrayList<Curvatureobject> Curvelist;
	public final ArrayList<Segmentobject> Seglist;
	
	public RegressionCurveSegment(final ArrayList<RegressionFunction> functionlist, final ArrayList<Curvatureobject> Curvelist, final ArrayList<Segmentobject> Seglist) {
		
		
		this.functionlist = functionlist;
		
		this.Curvelist = Curvelist;
		
		this.Seglist = Seglist;
		
		
	}
	

}
