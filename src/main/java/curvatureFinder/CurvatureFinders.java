package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import pluginTools.RegressionCurveSegment;

public interface CurvatureFinders<T extends RealType<T> & NativeType<T>> extends OutputAlgorithm<HashMap<Integer, RegressionCurveSegment> > {

}
