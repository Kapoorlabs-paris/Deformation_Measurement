package ellipsoidDetector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;
import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.ransac.RansacModels.*;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class FindEllipsoids {

	public static void main(String[] args) throws NotEnoughDataPointsException, IllDefinedDataPointsException {

		new ImageJ();

		ImagePlus imp = new Opener().openImage("/Users/varunkapoor/Documents/Bubbles/ThreeIntersections.tif");
		ImagePlus impA = new Opener().openImage("/Users/varunkapoor/Documents/Bubbles/ThreeIntersections.tif");

		RandomAccessibleInterval<FloatType> inputimage = ImageJFunctions.convertFloat(impA);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(inputimage), minval, maxval);
		// impA = ImageJFunctions.show(inputimage);
		imp.show();
		List<Pair<RealLocalizable, FloatType>> truths = new ArrayList<Pair<RealLocalizable, FloatType>>();

		ArrayList<EllipseRoi> ellipseList = new ArrayList<EllipseRoi>();

		float threshold = OtsuEllipsoid.AutomaticThresholding(inputimage);

		System.out.println("Threshold Value " + threshold);

		truths = ConnectedComponentCoordinates.GetCoordinates(inputimage, new FloatType(threshold));

		System.out.println("Initial set of points " + truths.size());
		Overlay ov = new Overlay();
		imp.setOverlay(ov);

		double outsideCutoffDistance = 2;
		double insideCutoffDistance = 2;
		double minpercent = 0.65;
        int maxiter =100;
        int radiusdetection = 5;
        Color colorDet = Color.GREEN;
		final int ndims = inputimage.numDimensions();
		final NumericalSolvers numsol = new BisectorEllipsoid();
		// Using the ellipse model to do the fitting
		ArrayList<Pair<Pair<Ellipsoid, GeneralEllipsoid>, List<Pair<RealLocalizable, FloatType>>>> Reducedsamples  = net.imglib2.algorithm.ransac.RansacModels.RansacEllipsoid
				.Allsamples(truths, outsideCutoffDistance, insideCutoffDistance, minpercent, numsol, maxiter, ndims);

		
		for (int i = 0; i < Reducedsamples.size(); ++i) {

			EllipseRoi ellipse = DisplayEllipse.create2DEllipse(Reducedsamples.get(i).getA().getA().getCenter(),
					new double[] { Reducedsamples.get(i).getA().getA().getCovariance()[0][0],
							Reducedsamples.get(i).getA().getA().getCovariance()[0][1],
							Reducedsamples.get(i).getA().getA().getCovariance()[1][1] });
			ellipseList.add(ellipse);
			ellipse.setStrokeColor(Color.RED);
			ellipse.setStrokeWidth(2);

			System.out.println("Center :" + Reducedsamples.get(i).getA().getA().getCenter()[0] + " "
					+ Reducedsamples.get(i).getA().getA().getCenter()[1] + " " + " Radius "
					+ Reducedsamples.get(i).getA().getA().getRadii()[0] + " "
					+ Reducedsamples.get(i).getA().getA().getRadii()[1]);
			ov.add(ellipse);

			
		}

		
		imp.updateAndDraw();

		
		// Obtain the points of intersections
		ArrayList<double[]> PointsIntersect = new ArrayList<double[]>();
		
		
		HashMap<Integer, Pair<GeneralEllipsoid, GeneralEllipsoid>> fitmap = new HashMap<Integer, Pair<GeneralEllipsoid, GeneralEllipsoid>>();
		
		for (int i = 0; i < Reducedsamples.size() ; ++i) {
		
			for (int j = 0; j < Reducedsamples.size() ; ++j) {
			
				
				if (j!=i)
			fitmap.put(Reducedsamples.get(i).getA().getB().hashCode() + Reducedsamples.get(j).getA().getB().hashCode() , new ValuePair<GeneralEllipsoid, GeneralEllipsoid>(Reducedsamples.get(i).getA().getB(),Reducedsamples.get(j).getA().getB() ));
			
		}
		}
		
		
		
		
		// Currently for the pair of Ellipses, to be improved for multiple intersecting points
		ArrayList<Integer> ellipsepairlist = new ArrayList<Integer>();
			
			
			
			
			

				for(Map.Entry<Integer, Pair<GeneralEllipsoid, GeneralEllipsoid>> entry : fitmap.entrySet()) {
					
					Pair<GeneralEllipsoid, GeneralEllipsoid> ellipsepair = entry.getValue();
					int sum = entry.getKey();
						boolean isfitted = false;
						for (int index = 0; index < ellipsepairlist.size(); ++index) {
						
							if(sum==ellipsepairlist.get(index))
							isfitted = true;
						
						}
						
						if(!isfitted)
						PointsIntersect.addAll(Intersections.PointsofIntersection(ellipsepair));
						ellipsepairlist.add(sum);
					

					
			
			
		
		}
		
		
		for (int i = 0; i < PointsIntersect.size(); ++i) {
			
			System.out.println(PointsIntersect.get(i)[0] + " " + PointsIntersect.get(i)[1]);
			
           OvalRoi intersectionsRoi = new OvalRoi(PointsIntersect.get(i)[0] - radiusdetection, PointsIntersect.get(i)[1] - radiusdetection, 2 * radiusdetection , 2 * radiusdetection);	
           intersectionsRoi.setStrokeColor(colorDet);
           ov.add(intersectionsRoi);
			
		}
		imp.updateAndDraw();
		
	}

}
