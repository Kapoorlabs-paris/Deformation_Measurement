package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;

import ellipsoidDetector.Intersectionobject;
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
	public final ArrayList<Intersectionobject> AllCurveintersection; 
	public final ArrayList<Intersectionobject> AlldenseCurveintersection;
	HashMap<Integer, RegressionCurveSegment> BestDelta = new HashMap<Integer, RegressionCurveSegment>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	public ThreePointCircleFitter(final InteractiveSimpleEllipseFit parent, 
			ArrayList<Intersectionobject> AllCurveintersection, ArrayList<Intersectionobject> AlldenseCurveintersection,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, 
			final JProgressBar jpb, final int percent,
			final int celllabel,final int thirdDimension,final int fourthDimension ) {
		
		this.parent = parent;
		this.jpb = jpb;
		this.celllabel = celllabel;
		this.AllCurveintersection = AllCurveintersection;
		this.AlldenseCurveintersection = AlldenseCurveintersection;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
		this.ActualRoiimg = ActualRoiimg;
	}
	
	public void execute() {
		
		int ndims = ActualRoiimg.numDimensions();
		
		String uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		
		
		List<RealLocalizable> truths = GetCandidatePoints.ListofPoints(parent, ActualRoiimg, jpb, percent, fourthDimension, thirdDimension);
		
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);

		// Get the sparse list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(truths, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);
		
		ComputeinSegments.OverSliderLoop(parent,  Ordered.getB(), centerpoint, truths,  AllCurveintersection,
				AlldenseCurveintersection, ndims, celllabel,fourthDimension, thirdDimension);
		
	}
	
	
}
