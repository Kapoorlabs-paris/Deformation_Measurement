package curvatureUtils;

import java.util.ArrayList;
import fiji.tool.SliceListener;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import imageAxis.AxisRendering;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;

public class ParallelResultDisplay {

	/**
	 * This parallel code processes nThreads image slices at once to create a final
	 * result N Dimension image of the same size as the original image
	 * 
	 */
	final InteractiveSimpleEllipseFit parent;
	final ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv;
	public ImagePlus imp;
	RandomAccessibleInterval<FloatType> probImg;
	boolean show;
	// Create a constructor


	public ParallelResultDisplay(InteractiveSimpleEllipseFit parent, ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv, boolean show) {

		this.parent = parent;
		this.currentresultCurv = currentresultCurv;
		probImg = new ArrayImgFactory<FloatType>().create( parent.originalimgbefore, new FloatType() );
	    this.show = show;
		this.imp = ImageJFunctions.wrapFloat(probImg, "");
		//SliceObserver sliceObserver = new SliceObserver( this.imp, new ImagePlusListener() );
	}
	


	
	public RandomAccessibleInterval<FloatType> ResultDisplayCircleFit() {

		double minCurvature = Double.MAX_VALUE;
		double maxCurvature = Double.MIN_VALUE;

		// Get absolute max and min Curvature values for all times
		for (int index = 0; index < currentresultCurv.size(); ++index) {

			Pair<String, Pair<Integer, ArrayList<double[]>>> currentpair = currentresultCurv.get(index);

			double[] Curvature = new double[currentpair.getB().getB().size()];

			for (int i = 0; i < currentpair.getB().getB().size(); ++i) {

				Curvature[i] = currentpair.getB().getB().get(i)[2];

				if (Curvature[i] <= minCurvature)
					minCurvature = Curvature[i];
				if (Curvature[i] >= maxCurvature)
					maxCurvature = Curvature[i];
			}

			int time = currentpair.getB().getA();

			ArrayList<double[]> TimeCurveList = currentpair.getB().getB();

			RandomAccessibleInterval<FloatType> CurrentViewprobImg = utility.Slicer.getCurrentView(probImg, time,
					parent.thirdDimensionSize, 1, parent.fourthDimensionSize);
			new ProcessSliceDisplayCircleFit(CurrentViewprobImg, TimeCurveList, minCurvature, maxCurvature).run();
		}

		final Cursor<FloatType> cursor = Views.iterable(probImg).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			double lambda = (cursor.getFloatPosition(0) - probImg.min(0)) / (probImg.max(0) - probImg.min(0));
			if (cursor.getDoublePosition(1) >= probImg.max(1) - probImg.min(1) - 5)
				cursor.get().set((float) (0 + lambda * (2 * maxCurvature - 0)));

		}
	    if(show)
		AxisRendering.Reshape(probImg, "Curvature Color coded");
		IJ.run("Fire");
		
		return probImg;

	}
	
	
	/**
	    * Generic, type-agnostic method to create an identical copy of an Img
	    *
	    * @param input - the Img to copy
	    * @return - the copy of the Img
	    */
	   public Img<FloatType> copyImage( final RandomAccessibleInterval< FloatType > input )
	   {
	       // create a new Image with the same properties
	       // note that the input provides the size for the new image as it implements
	       // the Interval interface
	       Img< FloatType > output = new ArrayImgFactory<FloatType>().create( input, new FloatType() );

	       // create a cursor for both images
	       Cursor< FloatType> cursorInput = Views.iterable(input).cursor();
	       Cursor< FloatType > cursorOutput = output.cursor();

	       // iterate over the input
	       while ( cursorInput.hasNext())
	       {
	           // move both cursors forward by one pixel
	           cursorInput.fwd();
	           cursorOutput.fwd();

	           // set the value of this pixel of the output image to the same as the input,
	           // every Type supports T.set( T type )
	           cursorOutput.get().set( cursorInput.get() );
	       }

	       // return the copy
	       return output;
	   }
	
	
	
	
}