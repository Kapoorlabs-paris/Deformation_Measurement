package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import curvatureUtils.ClockDisplayer;
import ellipsoidDetector.Intersectionobject;
import net.imglib2.RealLocalizable;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;

public class CurvatureFinderComboMethod<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
implements CurvatureFinders<T>  {

	@Override
	public ConcurrentHashMap<Integer, RegressionCurveSegment> getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean process() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RegressionLineProfile getLocalcurvature(ArrayList<double[]> Cordlist, RealLocalizable centerpoint,
			int strideindex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<RegressionLineProfile, ClockDisplayer> getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection,
			HashMap<Integer, Intersectionobject> AlldenseCurveintersection, int ndims, int celllabel, int t, int z) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	

}
