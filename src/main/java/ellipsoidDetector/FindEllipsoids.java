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

		ImagePlus impA = new Opener().openImage("/home/varun/sampleimages/smallCircles.tif");
		impA.show();
		RandomAccessibleInterval<FloatType> inputimage = ImageJFunctions.convertFloat(impA);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(inputimage), minval, maxval);

		int nsamples = 0;

		final List<RealLocalizable> truths = new ArrayList<RealLocalizable>();

		ArrayList<EllipseRoi> ellipseList = new ArrayList<EllipseRoi>();

		Cursor<FloatType> cursor = Views.iterable(inputimage).localizingCursor();
		final double[] posf = new double[inputimage.numDimensions() + 1];
		while (cursor.hasNext()) {

			cursor.fwd();
			cursor.localize(posf);
			final RealPoint rpos = new RealPoint(posf);

			if (cursor.get().get() > 0) {
				truths.add(rpos);
				nsamples++;
			}

		}

		Overlay ov = new Overlay();
        impA.setOverlay(ov);
		
		int outsideCutoffDistance = 3;
		int insideCutoffDistance = 3;

		// Using the ellipse model to do the fitting

	RoiManager roim = 	new RoiManager();
		if (nsamples > 0) {
			final ArrayList<Pair<Ellipsoid, List<RealLocalizable>>> Allsamples = net.imglib2.algorithm.ransac.RansacModels.RansacEllipsoid
					.Allsamples(truths, nsamples, outsideCutoffDistance, insideCutoffDistance);

			for (int i = 0; i < Allsamples.size(); ++i) {

				EllipseRoi ellipse = DisplayEllipse.create2DEllipse(Allsamples.get(i).getA().getCenter(),
						new double[] {Allsamples.get(i).getA().getCovariance()[0][0], Allsamples.get(i).getA().getCovariance()[0][1], Allsamples.get(i).getA().getCovariance()[1][1] });
				ellipseList.add(ellipse);
				ellipse.setStrokeColor(Color.RED);
				 roim.addRoi(ellipse);
				 
				System.out.println(Allsamples.get(i).getA().getCenter()[0] + " " + Allsamples.get(i).getA().getCenter()[1] + " " + Allsamples.get(i).getA().getRadii()[0] + " " + Allsamples.get(i).getA().getRadii()[2] 
						+ " " + Allsamples.get(i).getA().getCenter().length) ;
				ov.add(ellipse);
                
				
			}

		}
		

        
        
		impA.updateAndDraw();
		

	}

}
