package ellipsoidDetector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import distanceTransform.CreateDistanceTransform;
import distanceTransform.CreateWatershed;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;
import mpicbg.imglib.algorithm.fft.FourierTransform.PreProcessing;
import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.morphology.table2d.Thin;
import net.imglib2.algorithm.ransac.RansacModels.*;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import preProcessing.Otsu;


public class FindEllipsoids {

	public static void main(String[] args) throws NotEnoughDataPointsException, IllDefinedDataPointsException {

		new ImageJ();

		ImagePlus imp = new Opener().openImage("/Users/varunkapoor/Documents/Bubbles/RoiSet.tif");
		RandomAccessibleInterval<FloatType> inputimage = ImageJFunctions.convertFloat(imp);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(inputimage), minval, maxval);
		imp = ImageJFunctions.show(inputimage);

		List<Pair<RealLocalizable, FloatType>> truths = new ArrayList<Pair<RealLocalizable, FloatType>>();


		

		// Create Distance Transform Map
		float thresholdOrig = Otsu.AutomaticThresholding(inputimage);
		
		/*
		RandomAccessibleInterval<BitType> bitimg = Otsu.Getbinaryimage(inputimage, thresholdOrig);

		
		CreateDistanceTransform<FloatType> Dist = new CreateDistanceTransform<FloatType>(inputimage, bitimg);
		Dist.process();
		RandomAccessibleInterval<FloatType> inputimagePRE = 
				preProcessing.Kernels.CannyEdgeandMean(Dist.getResult(), 5);
		
		
		// Normalize the Distance Transformed image
		Normalize.normalize(Views.iterable(inputimagePRE), minval, maxval);
		float threshold = Otsu.AutomaticThresholding(inputimagePRE);
		System.out.println("Threshold Value " + threshold);

*/
/*
		// Watershed the image
		CreateWatershed<FloatType> Water = new CreateWatershed<>(inputimage, bitimg);
		Water.process();
		RandomAccessibleInterval<IntType> inputimageINT = Water.getResult();
		ImageJFunctions.show(inputimageINT);

		ImagePlus impPre = ImageJFunctions.show(inputimagePRE);

		impPre.setTitle("Distance Transformed image");
*/
		truths = ConnectedComponentCoordinates.GetCoordinates(inputimage,
				new FloatType(thresholdOrig));

		System.out.println("Initial set of points " + truths.size());
		Overlay ov = new Overlay();
		imp.setOverlay(ov);

		// To condider the points as inliers these variables have to be specified, if
		// large you allow for more error
		double outsideCutoffDistance = 2;
		double insideCutoffDistance = 2;
		// To consider what is a good ellipse, most of the ellipse points on the image
		// must lie on the detection, how much percent of points lie on it is specified
		// here
		double minpercent = 0.65;

		// Program rejects bad ellipses but in some cases there are no ellipses to be
		// found but other shapes are present, this parameter tells the program how many
		// max tries to find the ellipsoids
		int maxiter = 30;

		// In order to prevent overlapping detections, use this distance veto between
		// centers of detected ellipsoids
		final double minSeperation = 5;

		// Expected number of ellipses to be found in an image (Put large value if
		// uncertain)
		final int maxCircles = 15;

		int radiusdetection = 5;
		
		
		final int maxSize = 200;
		
		
		
		Color colorDet = Color.GREEN;
		Color colorLineA = Color.YELLOW;
		Color colorLineB = Color.YELLOW;
		final int ndims = inputimage.numDimensions();
		final NumericalSolvers numsol = new BisectorEllipsoid();

		// Using the ellipse model to do the fitting
		ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, FloatType>>>> Reducedsamples = net.imglib2.algorithm.ransac.RansacModels.RansacEllipsoid
				.Allsamples(truths, outsideCutoffDistance, insideCutoffDistance, minpercent, numsol, maxiter, ndims);

		SortSegments.Sort(Reducedsamples);
		for (int i = 0; i < Reducedsamples.size() - 1; ++i) {

			double[] center = Reducedsamples.get(i).getA().getCenter();

			double[] centernext = Reducedsamples.get(i + 1).getA().getCenter();

			double dist = Distance.DistanceSq(center, centernext);

			double[] radi = Reducedsamples.get(i).getA().getRadii();
			
			double maxradius = Math.max(radi[0], radi[1]);
			
			if (dist < minSeperation * minSeperation || maxradius > maxSize )
				Reducedsamples.remove(Reducedsamples.get(i));

		}

		for (int i = 0; i < Reducedsamples.size(); ++i) {

			EllipseRoi ellipse = DisplayasROI.create2DEllipse(Reducedsamples.get(i).getA().getCenter(),
					new double[] { Reducedsamples.get(i).getA().getCovariance()[0][0],
							Reducedsamples.get(i).getA().getCovariance()[0][1],
							Reducedsamples.get(i).getA().getCovariance()[1][1] });
			ellipse.setStrokeColor(Color.RED);
			ellipse.setStrokeWidth(1);

			System.out.println("Center :" + Reducedsamples.get(i).getA().getCenter()[0] + " "
					+ Reducedsamples.get(i).getA().getCenter()[1] + " " + " Radius "
					+ Reducedsamples.get(i).getA().getRadii()[0] + " " + Reducedsamples.get(i).getA().getRadii()[1]);
			ov.add(ellipse);

		}

		imp.updateAndDraw();

		// Obtain the points of intersections

		ArrayList<Tangentobject> AllPointsofIntersect = new ArrayList<Tangentobject>();

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

		// Currently for the pair of Ellipses, to be improved for multiple intersecting
		// points
		ArrayList<Integer> ellipsepairlist = new ArrayList<Integer>();

		for (Map.Entry<Integer, Pair<Ellipsoid, Ellipsoid>> entry : fitmapspecial.entrySet()) {

			Pair<Ellipsoid, Ellipsoid> ellipsepair = entry.getValue();

			int sum = entry.getKey();
			boolean isfitted = false;
			for (int index = 0; index < ellipsepairlist.size(); ++index) {

				if (sum == ellipsepairlist.get(index))
					isfitted = true;

			}

			if (!isfitted) {

				Tangentobject PointsIntersect = new Tangentobject(Intersections.PointsofIntersection(ellipsepair),
						fitmapspecial.get(sum), 0 ,0 );

				AllPointsofIntersect.add(PointsIntersect);
				ellipsepairlist.add(sum);

			}

		}

		for (int i = 0; i < AllPointsofIntersect.size(); ++i) {

			for (int j = 0; j < AllPointsofIntersect.get(i).Intersections.size(); ++j) {

				OvalRoi intersectionsRoi = new OvalRoi(
						AllPointsofIntersect.get(i).Intersections.get(j)[0] - radiusdetection,
						AllPointsofIntersect.get(i).Intersections.get(j)[1] - radiusdetection, 2 * radiusdetection,
						2 * radiusdetection);
				intersectionsRoi.setStrokeColor(colorDet);
				ov.add(intersectionsRoi);

				double[] lineparamA = Tangent2D.GetTangent(AllPointsofIntersect.get(i).ellipsepair.getA(),
						AllPointsofIntersect.get(i).Intersections.get(j));
				Line newlineA = DisplayasROI.create2DLine(lineparamA, AllPointsofIntersect.get(i).Intersections.get(j));
				newlineA.setStrokeColor(colorLineA);
				newlineA.setStrokeWidth(2);
				ov.add(newlineA);

				double[] lineparamB = Tangent2D.GetTangent(AllPointsofIntersect.get(i).ellipsepair.getB(),
						AllPointsofIntersect.get(i).Intersections.get(j));
				Line newlineB = DisplayasROI.create2DLine(lineparamB, AllPointsofIntersect.get(i).Intersections.get(j));
				newlineB.setStrokeColor(colorLineB);
				newlineB.setStrokeWidth(2);
				ov.add(newlineB);

			}

		}
		imp.updateAndDraw();

	}

}
