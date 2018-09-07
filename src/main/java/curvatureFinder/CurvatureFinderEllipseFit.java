package curvatureFinder;

import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;

import net.imglib2.RealLocalizable;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;

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
	HashMap<Integer, RegressionCurveSegment> BestDelta = new HashMap<Integer, RegressionCurveSegment>();;
	
	
	public CurvatureFinderEllipseFit(final InteractiveSimpleEllipseFit parent, final List<RealLocalizable> Ordered,final RealLocalizable centerpoint,final JProgressBar jpb, final int percent,
			final int celllabel,final int thirdDimension,final int fourthDimension ) {
		
		this.parent = parent;
		this.Ordered = Ordered;
		this.centerpoint = centerpoint;
		this.jpb = jpb;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
	}
	
	
	@Override
	public HashMap<Integer, RegressionCurveSegment> getResult() {

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

}
