package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JProgressBar;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;

public class CurvatureFinderEllipseFit<T extends RealType<T> & NativeType<T>> implements CurvatureFinders<T> {

	
	public final InteractiveSimpleEllipseFit parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final List<RealLocalizable> Ordered; 
	public final RealLocalizable centerpoint;
	private static final String BASE_ERROR_MSG = "[EllipseFit-]";
	protected String errorMessage;
	ConcurrentHashMap<Integer, RegressionCurveSegment> BestDelta = new ConcurrentHashMap<Integer, RegressionCurveSegment>();;
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	
	public CurvatureFinderEllipseFit(final InteractiveSimpleEllipseFit parent, final List<RealLocalizable> Ordered,final RealLocalizable centerpoint, final RandomAccessibleInterval<FloatType> ActualRoiimg,
			final JProgressBar jpb, final int percent,
			final int celllabel,final int thirdDimension,final int fourthDimension ) {
		
		this.parent = parent;
		this.Ordered = Ordered;
		this.centerpoint = centerpoint;
		this.jpb = jpb;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
		this.ActualRoiimg = ActualRoiimg;
	}
	
	

	@Override
	public ConcurrentHashMap<Integer, RegressionCurveSegment> getResult() {

		return BestDelta;
	}

	@Override
	public boolean checkInput() {
		if (parent.CurrentViewOrig.numDimensions() > 4) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 4D, make slices of your stack . Got "
					+ parent.CurrentViewOrig.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getErrorMessage() {
		
		return errorMessage;
	}


	@Override
	public RegressionLineProfile getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection,  HashMap<Integer,Intersectionobject>  AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public RegressionLineProfile getCircleLocalcurvature(ArrayList<double[]> Cordlist, RealLocalizable centerpoint,
			int strideindex) {
		// TODO Auto-generated method stub
		return null;
	}

}
