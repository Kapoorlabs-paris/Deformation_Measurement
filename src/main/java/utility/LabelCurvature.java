package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.*;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class LabelCurvature implements Runnable {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
	List<Pair<RealLocalizable, BitType>> truths;
	final int t;
	final int z;

	final int percent;
	final JProgressBar jpb;

	public LabelCurvature(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			List<Pair<RealLocalizable, BitType>> truths, final int t, final int z) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = null;
		this.percent = 0;
	}

	public LabelCurvature(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			List<Pair<RealLocalizable, BitType>> truths, final int t, final int z, final JProgressBar jpb, final int percent) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = jpb;
		this.percent = percent;
	}

	

	@Override
	public void run() {
		if(!parent.automode || !parent.supermode) {
		if (parent.fourthDimensionSize != 0)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.Accountedframes.entrySet().size()),
					"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = " + z
							+ "/" + parent.thirdDimensionSize);
		else
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
					"Fitting ellipses and computing angles T/Z = " + z + "/" + parent.thirdDimensionSize);
		}
		else {
			
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize),
					"Fitting ellipses and computing angles T = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
							+ parent.thirdDimensionSize);
		}
		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);

		if(parent.fourthDimensionSize > 1)
		parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension, parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		final int ndims = ActualRoiimg.numDimensions();
		
			
			
	
	}
	
	
	
	public List<RealLocalizable> getOrderedList(){
		
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		
		
		
		
		return orderedtruths;
	}
	
	
	
}
