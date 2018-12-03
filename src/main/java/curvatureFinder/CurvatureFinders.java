package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ellipsoidDetector.Intersectionobject;
import mpicbg.models.Point;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;

public interface CurvatureFinders<T extends RealType<T> & NativeType<T>>
		extends OutputAlgorithm<HashMap<Integer, RegressionCurveSegment>> {

	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex);

	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z);

}
