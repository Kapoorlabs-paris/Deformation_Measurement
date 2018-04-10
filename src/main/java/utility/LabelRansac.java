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

public class LabelRansac implements Runnable {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
	List<Pair<RealLocalizable, BitType>> truths;
	final int t;
	final int z;
	final ArrayList<EllipseRoi> resultroi;
	final ArrayList<OvalRoi> resultovalroi;
	final ArrayList<Line> resultlineroi;
	final ArrayList<Tangentobject> AllPointsofIntersect;
	final ArrayList<Intersectionobject> Allintersection;

	final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial;
	final boolean supermode;
	
	final JProgressBar jpb;

	public LabelRansac(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			List<Pair<RealLocalizable, BitType>> truths, final int t, final int z, ArrayList<EllipseRoi> resultroi,
			ArrayList<OvalRoi> resultovalroi, ArrayList<Line> resultlineroi,
			final ArrayList<Tangentobject> AllPointsofIntersect, final ArrayList<Intersectionobject> Allintersection,final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial, final boolean supermode) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.resultroi = resultroi;
		this.resultovalroi = resultovalroi;
		this.resultlineroi = resultlineroi;
		this.Allintersection = Allintersection;
		this.AllPointsofIntersect = AllPointsofIntersect;
		this.fitmapspecial = fitmapspecial;
		this.jpb = null;
		this.supermode = supermode;
	}

	public LabelRansac(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,
			List<Pair<RealLocalizable, BitType>> truths, final int t, final int z, ArrayList<EllipseRoi> resultroi,
			ArrayList<OvalRoi> resultovalroi, ArrayList<Line> resultlineroi,
			final ArrayList<Tangentobject> AllPointsofIntersect, final ArrayList<Intersectionobject> Allintersection,
			final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial, final JProgressBar jpb, final boolean supermode) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.resultroi = resultroi;
		this.resultovalroi = resultovalroi;
		this.resultlineroi = resultlineroi;
		this.Allintersection = Allintersection;
		this.AllPointsofIntersect = AllPointsofIntersect;
		this.fitmapspecial = fitmapspecial;
		this.jpb = jpb;
		this.supermode = supermode;
	}

	

	@Override
	public void run() {

		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);

		if(parent.fourthDimensionSize > 1)
		parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension, parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
		final int ndims = ActualRoiimg.numDimensions();
		final NumericalSolvers numsol = new BisectorEllipsoid();
		// Using the ellipse model to do the fitting
		ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, BitType>>>> Reducedsamples = RansacEllipsoid.Allsamples(
				truths, parent.outsideCutoff, parent.insideCutoff, parent.minpercent, parent.minperimeter, parent.maxperimeter, numsol, parent.maxtry, ndims);

		String uniqueID = Integer.toString(z) + Integer.toString(t);
		if (Reducedsamples != null) {
			SortSegments.Sort(Reducedsamples);
			for (int i = 0; i < Reducedsamples.size() - 1; ++i) {

				double[] center = Reducedsamples.get(i).getA().getCenter();

				double[] centernext = Reducedsamples.get(i + 1).getA().getCenter();

				double dist = Distance.DistanceSq(center, centernext);

				if (dist < parent.minSeperation * parent.minSeperation)
					Reducedsamples.remove(Reducedsamples.get(i));

			}

			for (int i = 0; i < Reducedsamples.size(); ++i) {

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

			int count = 0;
			if(!parent.automode) {
				
			ArrayList<Integer> ellipsepairlist = new ArrayList<Integer>();
			
			
			for (int i = 0; i < Reducedsamples.size(); ++i) {

				for (int j = 0; j < Reducedsamples.size(); ++j) {

					if (j != i) {

						ellipsepairlist.add(count);
						fitmapspecial.add(new ValuePair<Ellipsoid, Ellipsoid>(Reducedsamples.get(i).getA(),
								Reducedsamples.get(j).getA()));

						count++;
					}
				}
			}
			final ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecialred = new ArrayList<Pair<Ellipsoid, Ellipsoid>>();
			fitmapspecialred.addAll(fitmapspecial);
			
			
			for (int i = 0; i < fitmapspecialred.size(); ++i) {

				Pair<Ellipsoid, Ellipsoid> ellipsepairA = fitmapspecialred.get(i);

				for (int j = 0; j < fitmapspecialred.size(); ++j) {

					if (i != j) {
						Pair<Ellipsoid, Ellipsoid> ellipsepairB = fitmapspecialred.get(j);

						if (ellipsepairA.getA().hashCode() == (ellipsepairB.getB().hashCode())
								&& ellipsepairA.getB().hashCode() == (ellipsepairB.getA().hashCode())) {
							fitmapspecialred.remove(ellipsepairB);
							break;
						}

					}

				}

			}
			for (int i = 0; i < fitmapspecialred.size(); ++i) {

				Pair<Ellipsoid, Ellipsoid> ellipsepair = fitmapspecialred.get(i);

				ArrayList<double[]> pos = Intersections.PointsofIntersection(ellipsepair);

				
				
				
				
				Tangentobject PointsIntersect = new Tangentobject(pos, ellipsepair, t, z);

				for (int j = 0; j < pos.size(); ++j) {

					OvalRoi intersectionsRoi = new OvalRoi(pos.get(j)[0] - parent.radiusdetection,
							pos.get(j)[1] - parent.radiusdetection, 2 * parent.radiusdetection,
							2 * parent.radiusdetection);
					intersectionsRoi.setStrokeColor(parent.colorDet);
					resultovalroi.add(intersectionsRoi);

					double[] lineparamA = Tangent2D.GetTangent(ellipsepair.getA(), pos.get(j));

					double[] lineparamB = Tangent2D.GetTangent(ellipsepair.getB(), pos.get(j));

					Angleobject angleobject = Tangent2D.GetTriAngle(lineparamA, lineparamB, pos.get(j), ellipsepair);
					resultlineroi.add(angleobject.lineA);
					resultlineroi.add(angleobject.lineB);
					

					Intersectionobject currentintersection = new Intersectionobject(pos.get(j), angleobject.angle,
							ellipsepair, resultlineroi, t, z);

					Allintersection.add(currentintersection);

					System.out.println("Angle: " + angleobject.angle + " " + pos.get(j)[0]);

				}

				AllPointsofIntersect.add(PointsIntersect);


			}
			parent.ALLIntersections.put(uniqueID, Allintersection);
			

			

			// Add new result rois to ZTRois
			for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				if (currentobject.fourthDimension == t && currentobject.thirdDimension == z) {

					currentobject.resultroi = resultroi;
					currentobject.resultovalroi = resultovalroi;
					currentobject.resultlineroi = resultlineroi;

				}

			}

			
			}
			parent.superReducedSamples.addAll(Reducedsamples);
			
			

			if (parent.automode && !parent.redoing) {
				
				Roiobject currentobject = new Roiobject(resultroi,resultovalroi,resultlineroi, z, t, true);
				parent.ZTRois.put(uniqueID, currentobject);

				DisplayAuto.Display(parent);
			}
			
			
		} else
			return;
	}
	
}
