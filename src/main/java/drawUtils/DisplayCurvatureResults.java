package drawUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.process.LUT;
import net.imagej.display.ColorTables;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.display.ColorTable8;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import script.imglib.color.RGBA;

public class DisplayCurvatureResults {

	static LUT lut = LUT.createLutFromColor(Color.BLUE);

	public static RandomAccessibleInterval<FloatType> Display(InteractiveSimpleEllipseFit parent,
			RandomAccessibleInterval<FloatType> originalimg,
			ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv) {
		System.gc();
		RandomAccessibleInterval<FloatType> probImg = new ArrayImgFactory<FloatType>().create(originalimg,
				new FloatType());

		double minIntensity = Double.MAX_VALUE;
		double maxIntensity = Double.MIN_VALUE;
		for (int index = 0; index < currentresultCurv.size(); ++index) {

			Pair<String, Pair<Integer, ArrayList<double[]>>> currentpair = currentresultCurv.get(index);

			int time = currentpair.getB().getA();

			double[] X = new double[currentpair.getB().getB().size()];
			double[] Y = new double[currentpair.getB().getB().size()];
			double[] I = new double[currentpair.getB().getB().size()];

			for (int i = 0; i < currentpair.getB().getB().size(); ++i) {

				X[i] = currentpair.getB().getB().get(i)[0];
				Y[i] = currentpair.getB().getB().get(i)[1];
				I[i] = currentpair.getB().getB().get(i)[2];

				if(I[i] <= minIntensity)
					minIntensity = I[i];
				if(I[i] >= maxIntensity)
				    maxIntensity = I[i];
			}

			RandomAccessibleInterval<FloatType> CurrentViewprobImg = utility.Slicer.getCurrentView(probImg, time,
					parent.thirdDimensionSize, 1, parent.fourthDimensionSize);

			final Cursor<FloatType> cursor = Views.iterable(CurrentViewprobImg).localizingCursor();

			while (cursor.hasNext()) {

				cursor.fwd();

				for (int i = 0; i < X.length; ++i) {

					if ((Math.abs(cursor.getFloatPosition(0) - X[i])) == 0
							&& (Math.abs(cursor.getFloatPosition(1) - Y[i])) == 0) {

						cursor.get().setReal(I[i]);

					}
					
					

				}

			}
			
			
			final Cursor<FloatType> seccursor = Views.iterable(probImg).localizingCursor();

			while (seccursor.hasNext()) {

				seccursor.fwd();

				double Xcord = seccursor.getDoublePosition(0);
				double lambda = (Xcord - probImg.min(0) ) / (probImg.max(0) - probImg.min(0));
				if(seccursor.getDoublePosition(1) >= probImg.max(1) - probImg.min(1) - 5)
					seccursor.get().setReal( minIntensity + lambda * (maxIntensity - minIntensity));
				
				
				
			}
			

		}

		return probImg;

	}
	  public static void computeMinMax(
		        final Iterable< FloatType > input, final FloatType min,  final FloatType max )
		    {
		        // create a cursor for the image (the order does not matter)
		        final Iterator< FloatType > iterator = input.iterator();
		 
		        // initialize min and max with the first image value
		        FloatType type = iterator.next();
		 
		        min.set( type );
		        max.set( type );
		 
		        // loop over the rest of the data and determine min and max value
		        while ( iterator.hasNext() )
		        {
		            // we need this type more than once
		            type = iterator.next();
		 
		            if ( type.compareTo( min ) < 0 )
		                min.set( type );
		            
		 
		            if ( type.compareTo( max ) > 0 )
		                max.set( type );
		        }
		    }


}
