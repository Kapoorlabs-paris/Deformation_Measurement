package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JProgressBar;

import curvatureUtils.ClockDisplayer;
import ellipsoidDetector.Intersectionobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;

public class CurvatureFinderComboMethod<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
implements CurvatureFinders<T>  {
	
	
	public final InteractiveSimpleEllipseFit parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final ArrayList<Intersectionobject> AllCurveintersection;
	public final HashMap<Integer, Intersectionobject> AlldenseCurveintersection;
	ConcurrentHashMap<Integer, RegressionCurveSegment> Bestdelta = new ConcurrentHashMap<Integer, RegressionCurveSegment>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	private final String BASE_ERROR_MSG = "[Combo-Method-]";
	protected String errorMessage;
	
	
	public CurvatureFinderComboMethod(final InteractiveSimpleEllipseFit parent,
			ArrayList<Intersectionobject> AllCurveintersection,HashMap<Integer, Intersectionobject> AlldenseCurveintersection,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, final JProgressBar jpb, final int percent,
			final int celllabel, final int thirdDimension, final int fourthDimension) {

		this.parent = parent;
		this.AllCurveintersection = AllCurveintersection;
		this.AlldenseCurveintersection = AlldenseCurveintersection;
		this.jpb = jpb;
		this.ActualRoiimg = ActualRoiimg;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
	}
	
	
	public HashMap<Integer, Intersectionobject> getMap() {

		return AlldenseCurveintersection;
	}
	
	@Override
	public ConcurrentHashMap<Integer, RegressionCurveSegment> getResult() {

		return Bestdelta;
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
	public String getErrorMessage() {
		
		return errorMessage;
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
