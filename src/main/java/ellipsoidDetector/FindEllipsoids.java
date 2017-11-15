package ellipsoidDetector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
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
import net.imglib2.view.Views;

public class FindEllipsoids {

	public static void main(String[] args) throws NotEnoughDataPointsException, IllDefinedDataPointsException {

		new ImageJ();

		ImagePlus impA = new Opener().openImage("/home/varun/sampleimages/TwoCircles.tif");
		
		RandomAccessibleInterval<FloatType> inputimage = ImageJFunctions.convertFloat(impA);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(inputimage), minval, maxval);
        impA = ImageJFunctions.show(inputimage);
		int nsamples = 0;

		final List<RealLocalizable> truths = new ArrayList<RealLocalizable>();

		ArrayList<EllipseRoi> ellipseList = new ArrayList<EllipseRoi>();

		Cursor<FloatType> cursor = Views.iterable(inputimage).localizingCursor();
		final double[] posf = new double[inputimage.numDimensions() + 1];
		
		
		float threshold = OtsuEllipsoid.AutomaticThresholding(inputimage);
		
		System.out.println("Threshold Value " + threshold);
		
		while (cursor.hasNext()) {

			cursor.fwd();
			cursor.localize(posf);
			final RealPoint rpos = new RealPoint(posf);

			if (cursor.get().get() > threshold) {
				truths.add(rpos);
				nsamples++;
			}

		}

		Overlay ov = new Overlay();
        impA.setOverlay(ov);
		
		double outsideCutoffDistance = 2.5;
		double insideCutoffDistance = 2.5;
		double minSize = 0;
		double maxSize = 55500;
		double maxdist = 1;
		double minpoints = 50;
    
		// Using the ellipse model to do the fitting
		ArrayList<Pair<Ellipsoid, List<RealLocalizable>>> Reducedsamples = new ArrayList<Pair<Ellipsoid, List<RealLocalizable>>>(); 
		if (nsamples > 0) {
			final ArrayList<Pair<Ellipsoid, List<RealLocalizable>>> Allsamples = net.imglib2.algorithm.ransac.RansacModels.RansacEllipsoid
					.Allsamples(truths, nsamples, outsideCutoffDistance, insideCutoffDistance, maxSize, minSize, minpoints);
			// Exclusion criteria
			for (int i = 0; i < Allsamples.size(); ++i) {
				
				double size2D = 1;

				for (int j = 0; j < Allsamples.get(i).getA().getRadii().length; ++j) {
			
					if (Math.abs(Allsamples.get(i).getA().getRadii()[j]) < Double.MAX_VALUE) {
					
					size2D*= Allsamples.get(i).getA().getRadii()[j];
				
					
					}
				}
				
				if (size2D > minSize && size2D < maxSize ) {
					
					Reducedsamples.add(Allsamples.get(i));
					System.out.println("Accepting Ellipse at " + " (" + Allsamples.get(i).getA().getCenter()[0] + ", " +   Allsamples.get(i).getA().getCenter()[1]  + ", " +  Allsamples.get(i).getA().getCenter()[2] + " )"  );
					
				}
				
				
				
			
			}
			
			
			
			for (int i = 0; i < Reducedsamples.size(); ++i) {

				EllipseRoi ellipse = DisplayEllipse.create2DEllipse(Reducedsamples.get(i).getA().getCenter(),
						new double[] {Reducedsamples.get(i).getA().getCovariance()[0][0], Reducedsamples.get(i).getA().getCovariance()[0][1], Reducedsamples.get(i).getA().getCovariance()[1][1] });
				ellipseList.add(ellipse);
				ellipse.setStrokeColor(Color.RED);
				ellipse.setStrokeWidth(2);
				 
				System.out.println("Center :" + Reducedsamples.get(i).getA().getCenter()[0] + " " + Reducedsamples.get(i).getA().getCenter()[1] + " "  
				+ " Radius "+ Reducedsamples.get(i).getA().getRadii()[0] + " " + Reducedsamples.get(i).getA().getRadii()[2] + " " + Reducedsamples.get(i).getA().getRadii()[1]) ;
				ov.add(ellipse);
                
				
			}

		}
		

        
		impA.updateAndDraw();
		

	}

}
