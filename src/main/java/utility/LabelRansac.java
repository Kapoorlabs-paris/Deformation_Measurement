package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.imglib2.algorithm.ransac.RansacModels.Intersections;
import net.imglib2.algorithm.ransac.RansacModels.NumericalSolvers;
import net.imglib2.algorithm.ransac.RansacModels.SortSegments;
import net.imglib2.algorithm.ransac.RansacModels.Tangent2D;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveEllipseFit;

public class LabelRansac implements Runnable {

	final InteractiveEllipseFit parent;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
    List<Pair<RealLocalizable, BitType>> truths;
	final int t;
	final int z;
	final ArrayList<EllipseRoi> resultroi;
	final ArrayList<OvalRoi> resultovalroi;
	final 	ArrayList<Line> resultlineroi;
    
	public LabelRansac(final InteractiveEllipseFit parent, final RandomAccessibleInterval<BitType> ActualRoiimg,  List<Pair<RealLocalizable, BitType>> truths, final int t, final int z, ArrayList<EllipseRoi> resultroi,ArrayList<OvalRoi> resultovalroi, ArrayList<Line> resultlineroi  ) {
		
		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.resultroi = resultroi;
		this.resultovalroi = resultovalroi;
		this.resultlineroi = resultlineroi;
	}
	@Override
	public void run()  {
		
		truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);
		
		

		final int ndims = ActualRoiimg.numDimensions();
		final NumericalSolvers numsol = new BisectorEllipsoid();
		// Using the ellipse model to do the fitting
		ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, BitType>>>> Reducedsamples = net.imglib2.algorithm.ransac.RansacModels.RansacEllipsoid
				.Allsamples(truths, parent.outsideCutoff, parent.insideCutoff, parent.minpercent, numsol, parent.maxtry, ndims,
						parent.maxEllipses);

		SortSegments.Sort(Reducedsamples);
		for (int i = 0; i < Reducedsamples.size() - 1; ++i) {

			double[] center = Reducedsamples.get(i).getA().getCenter();

			double[] centernext = Reducedsamples.get(i + 1).getA().getCenter();

			double dist = Distance.DistanceSq(center, centernext);

			double[] radi = Reducedsamples.get(i).getA().getRadii();
			
			/*  Maybe another constraint
			double maxradius = Math.max(radi[0], radi[1]);
			|| maxradius > parent.maxSize
			*/
			if (dist < parent.minSeperation * parent.minSeperation  )
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
						+ Reducedsamples.get(i).getA().getRadii()[0] + " " + Reducedsamples.get(i).getA().getRadii()[1]);

			}
            
		
		
		// Obtain the points of intersections

		ArrayList<Tangentobject> AllPointsofIntersect = new ArrayList<Tangentobject>();
		ArrayList<Intersectionobject> Allintersection = new ArrayList<Intersectionobject>();

		HashMap<Integer, Pair<Ellipsoid, Ellipsoid>> fitmapspecial = new HashMap<Integer, Pair<Ellipsoid, Ellipsoid>>();
		for (int i = 0; i < Reducedsamples.size(); ++i) {

			for (int j = 0; j < Reducedsamples.size(); ++j) {

				if (j != i) {

					fitmapspecial.put(Reducedsamples.get(i).getA().hashCode() + Reducedsamples.get(j).getA().hashCode(),
							new ValuePair<Ellipsoid, Ellipsoid>(Reducedsamples.get(i).getA(),
									Reducedsamples.get(j).getA()));

				}
			}
		}

		
		ArrayList<Integer> ellipsepairlist = new ArrayList<Integer>();
		boolean isfitted = false;
		for (Map.Entry<Integer, Pair<Ellipsoid, Ellipsoid>> entry : fitmapspecial.entrySet()) {

			Pair<Ellipsoid, Ellipsoid> ellipsepair = entry.getValue();

			int sum = entry.getKey();
			
			for (int index = 0; index < ellipsepairlist.size(); ++index) {

				if (sum == ellipsepairlist.get(index))
					isfitted = true;

			}

			if (!isfitted) {

				
				ArrayList<double[]> pos = Intersections.PointsofIntersection(ellipsepair);
				
				Tangentobject PointsIntersect = new Tangentobject(pos,
						fitmapspecial.get(sum), t, z);

				
				
				for (int j = 0; j < pos.size(); ++j) {
					
					OvalRoi intersectionsRoi = new OvalRoi(
							pos.get(j)[0] - parent.radiusdetection,
							pos.get(j)[1] - parent.radiusdetection, 2 * parent.radiusdetection,
							2 * parent.radiusdetection);
					intersectionsRoi.setStrokeColor(parent.colorDet);
					resultovalroi.add(intersectionsRoi);
					

					double[] lineparamA = Tangent2D.GetTangent(ellipsepair.getA(),
							pos.get(j));
					Line newlineA = DisplayasROI.create2DLine(lineparamA, pos.get(j));
					newlineA.setStrokeColor(parent.colorLineA);
					newlineA.setStrokeWidth(2);
					resultlineroi.add(newlineA);

					double[] lineparamB = Tangent2D.GetTangent(ellipsepair.getB(),
							pos.get(j));
					Line newlineB = DisplayasROI.create2DLine(lineparamB, pos.get(j));
					newlineB.setStrokeColor(parent.colorLineB);
					newlineB.setStrokeWidth(2);
					resultlineroi.add(newlineB);
					
					
					double angle = Tangent2D.GetAngle(lineparamA, lineparamB);
					

					Intersectionobject currentintersection = new Intersectionobject(pos.get(j), angle, ellipsepair, t, z);
					
					Allintersection.add(currentintersection);
					
					
					System.out.println(angle);
					
				}
				
				AllPointsofIntersect.add(PointsIntersect);
				ellipsepairlist.add(sum);

			}

		}
	
		String uniqueID = Integer.toString(z) + Integer.toString(t);
        
		
		
		if (parent.ALLIntersections.get(uniqueID) == null) {
			
			parent.ALLIntersections.put(uniqueID, Allintersection);
		}
		
		else {
			
			
			parent.ALLIntersections.remove(uniqueID);
			parent.ALLIntersections.put(uniqueID, Allintersection);
		}
		
		
		
		
		   // Add new result rois to ZTRois
		for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

			Roiobject currentobject = entry.getValue();

			if (currentobject.fourthDimension == t
					&& currentobject.thirdDimension == z) {

				currentobject.resultroi = resultroi;
				currentobject.resultovalroi = resultovalroi;
				currentobject.resultlineroi = resultlineroi;
			
			}

		}
		
	}

	
}
