package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JProgressBar;

import curvatureUtils.DisplaySelected;
import curvatureUtils.InterpolateCurvature;
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
	final int celllabel;
	final int percent;
	final ArrayList<Line> resultlineroi;
	final JProgressBar jpb;

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<BitType> ActualRoiimg, List<Pair<RealLocalizable, BitType>> truths,
			ArrayList<Line> resultlineroi, final int t, final int z, final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = null;
		this.percent = 0;
		this.resultlineroi = resultlineroi;
		this.celllabel = celllabel;
	}

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<BitType> ActualRoiimg, List<Pair<RealLocalizable, BitType>> truths,
			ArrayList<Line> resultlineroi, final int t, final int z, final JProgressBar jpb, final int percent,
			final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.resultlineroi = resultlineroi;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = jpb;
		this.percent = percent;
		this.celllabel = celllabel;
	}

	@Override
	public void run() {
		if (!parent.curveautomode || !parent.curvesupermode) {
			if (parent.fourthDimensionSize != 0)
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.Accountedframes.entrySet().size()),
						"Computing Curvature = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
								+ parent.thirdDimensionSize);
			else if(parent.thirdDimensionSize!=0)
				utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.AccountedZ.entrySet().size()),
						"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		} else {

			utility.ProgressBar.SetProgressBar(jpb, 100 ,
					"Computing Curvature ");
		}
		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);

		// Get the sparse list of points
		List<RealLocalizable> orderedtruths = Listordereing.getOrderedList(truths, parent.deltasep);
		
		

		if (parent.fourthDimensionSize > 1)
			parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
					parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		final int ndims = ActualRoiimg.numDimensions();

		parent.localCurvature = CurvatureFunction.getCurvature(orderedtruths, ndims, celllabel, t, z);
		
		parent.AlllocalCurvature.add(parent.localCurvature);


	}

}
