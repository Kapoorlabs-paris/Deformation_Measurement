package curvatureFinder;

import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import utility.Listordereing;

public class ThreePointCircleFitter {

	
	
	public final InteractiveSimpleEllipseFit parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final List<RealLocalizable> Ordered; 
	public final RealLocalizable centerpoint;
	HashMap<Integer, RegressionCurveSegment> BestDelta = new HashMap<Integer, RegressionCurveSegment>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	public ThreePointCircleFitter(final InteractiveSimpleEllipseFit parent, final List<RealLocalizable> Ordered,final RealLocalizable centerpoint, final RandomAccessibleInterval<FloatType> ActualRoiimg, 
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
	
	public void execute() {
		
		
		List<RealLocalizable> truths = GetCandidatePoints.ListofPoints(parent, ActualRoiimg, jpb, percent, fourthDimension, thirdDimension);
		
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);

		// Get the sparse list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(truths, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered);
		
	}
	
	
}
