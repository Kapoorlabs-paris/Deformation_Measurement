package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JProgressBar;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.BisectorEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.ConnectedComponentCoordinates;
import net.imglib2.algorithm.ransac.RansacModels.DisplayasROI;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.algorithm.ransac.RansacModels.NumericalSolvers;
import net.imglib2.algorithm.ransac.RansacModels.RansacEllipsoid;
import net.imglib2.algorithm.ransac.RansacModels.SortSegments;
import net.imglib2.algorithm.ransac.RansacModels.CircleFits.PointSphere;
import net.imglib2.type.logic.BitType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;

public class LabelFit implements Runnable {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
	List<Pair<RealLocalizable, BitType>> truths;
	final int t;
	final int z;
	final ArrayList<EllipseRoi> resultroi;

	final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial;

	final JProgressBar jpb;

	
	// Fit
	public LabelFit(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			List<Pair<RealLocalizable, BitType>> truths, final int t, final int z, ArrayList<EllipseRoi> resultroi,
			final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.resultroi = resultroi;
		this.fitmapspecial = fitmapspecial;
		this.jpb = null;
	}

	public LabelFit(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			List<Pair<RealLocalizable, BitType>> truths, final int t, final int z, ArrayList<EllipseRoi> resultroi,
			final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial, final JProgressBar jpb) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.resultroi = resultroi;
		this.fitmapspecial = fitmapspecial;
		this.jpb = jpb;
	}

	@Override
	public void run() {

		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);

		// Extract three neighbouring points
		
		// Order the list
	//	OrderList();
		
		
		
		
		if (parent.fourthDimensionSize > 1)
			parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
					parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		
		final int ndims = ActualRoiimg.numDimensions();
		
		final NumericalSolvers numsol = new BisectorEllipsoid();
		// Using the circle model to do the fitting
		int minpoints = 9;
		int startindex = 0;
		int endindex = startindex + minpoints;
		
		List<Pair<RealLocalizable, BitType>> skiptruths = SkipIndices(truths);
		do {
		
		endindex = startindex + minpoints;
	
		System.out.println(endindex + " " + startindex);
		
		List<Pair<RealLocalizable, BitType>> smalltruths = ExtractSublist(skiptruths,startindex, endindex);
		
		
		
		
		ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, BitType>>>> Reducedsamples = PointSphere.Allsamples(
				smalltruths, numsol, parent.maxtry, ndims);

		String uniqueID = Integer.toString(z) + Integer.toString(t);
		if (Reducedsamples != null) {
			SortSegments.Sort(Reducedsamples);
	
			for (int i = 0; i <= Reducedsamples.size(); ++i) {

				EllipseRoi ellipse = DisplayasROI.create2DEllipse(Reducedsamples.get(i).getA().getCenter(),
						new double[] { Reducedsamples.get(i).getA().getCovariance()[0][0],
								Reducedsamples.get(i).getA().getCovariance()[0][1],
								Reducedsamples.get(i).getA().getCovariance()[1][1] });

				resultroi.add(ellipse);

				System.out.println("Center :" + Reducedsamples.get(i).getA().getCenter()[0] + " "
						+ Reducedsamples.get(i).getA().getCenter()[1] + " " + " Radius "
						+ Reducedsamples.get(i).getA().getRadii()[0] + " " + Reducedsamples.get(i).getA().getRadii()[1]
						+ "time " + "  " + t + " " + "Z" + " " + z);

			}

			parent.superReducedSamples.addAll(Reducedsamples);
			Roiobject currentobject = new Roiobject(resultroi,z, t, true);
			parent.ZTRois.put(uniqueID, currentobject);
			// Put display here
			DisplayAuto.Display(parent);
		} else
			return;
		
		startindex = endindex;
		System.out.println("hhh");
		}while(endindex <= skiptruths.size());
	}
	
	
	Comparator<Pair<RealLocalizable, BitType>> ComparebyX = new Comparator<Pair<RealLocalizable, BitType>>() {

		@Override
		public int compare(Pair<RealLocalizable, BitType> o1, Pair<RealLocalizable, BitType> o2) {

			return (int)Math.round(o1.getA().getDoublePosition(0) - o2.getA().getDoublePosition(0));
			
		}
		
		
		
	};
	
	Comparator<Pair<RealLocalizable, BitType>> ComparebyY = new Comparator<Pair<RealLocalizable, BitType>>() {

		@Override
		public int compare(Pair<RealLocalizable, BitType> o1, Pair<RealLocalizable, BitType> o2) {

			return (int)Math.round(o1.getA().getDoublePosition(1) - o2.getA().getDoublePosition(1));
			
		}
		
		
		
	};
	
	public List<Pair<RealLocalizable, BitType>> SkipIndices(List<Pair<RealLocalizable, BitType>> truths){
		
		int skip = (int) Math.round(parent.deltasep);
		List<Pair<RealLocalizable, BitType>> skiptruths = new ArrayList<Pair<RealLocalizable, BitType>>();
		if (skip == 0)
			skip = 1;
		
		for (int index = 0; index < truths.size(); index+=skip) {
			
			skiptruths.add(truths.get(index));
			
		}
		
		return skiptruths;
	}
	
	public List<Pair<RealLocalizable, BitType>> ExtractSublist(List<Pair<RealLocalizable, BitType>> truths, int startindex, int endindex){
		
		List<Pair<RealLocalizable, BitType>> smalltruths = new ArrayList<Pair<RealLocalizable, BitType>>();
		
        smalltruths = truths.subList(startindex, endindex);
		
		return smalltruths;
	}
	
	public void OrderList() {
		
		Collections.sort(truths, ComparebyX);
		Collections.sort(truths, ComparebyY);
		
		
	}

}
