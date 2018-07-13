package curvatureUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;

public class ParallelResultDisplay  {

	/**
	 * This parallel code processes nThreads image slices at once to create a final result N Dimension image of the same size as the original image
	 * 
	 */
	final InteractiveSimpleEllipseFit parent;
	final ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv;
	
	// Create a constructor
	public  ParallelResultDisplay(InteractiveSimpleEllipseFit parent,  ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv ) {
           
		this.parent = parent;
	    this.currentresultCurv = currentresultCurv;
	}
	

	public void ResultDisplay() {
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		
        // Make an image of same size as the original image in N-dimension
		RandomAccessibleInterval<FloatType> probImg = new ArrayImgFactory<FloatType>().create(parent.originalimgbefore,
				new FloatType());
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

		double minIntensity = Double.MAX_VALUE;
		double maxIntensity = Double.MIN_VALUE;

		// Get absolute max and min Curvature values for all times
		for (int index = 0; index < currentresultCurv.size(); ++index) {

			Pair<String, Pair<Integer, ArrayList<double[]>>> currentpair = currentresultCurv.get(index);

			double[] I = new double[currentpair.getB().getB().size()];

			for (int i = 0; i < currentpair.getB().getB().size(); ++i) {

				I[i] = currentpair.getB().getB().get(i)[2];

				if (I[i] <= minIntensity)
					minIntensity = I[i];
				if (I[i] >= maxIntensity)
					maxIntensity = I[i];
			}
		}

		// Iterate over the time points computation was performed
		while (itZ.hasNext()) {

			int time = itZ.next().getValue();

			ArrayList<double[]> TimeCurveList = parent.HashresultCurvature.get(time);

			RandomAccessibleInterval<FloatType> CurrentViewprobImg = utility.Slicer.getCurrentView(probImg, time,
					parent.thirdDimensionSize, 1, parent.fourthDimensionSize);

			// Add all the tasks to be executed in parallel
			tasks.add(Executors
					.callable(new ProcessSliceDisplay(CurrentViewprobImg, TimeCurveList, minIntensity, maxIntensity)));

		}

		try {
			taskExecutor.invokeAll(tasks);

			ImagePlus imp = ImageJFunctions.show(probImg);
			imp.setTitle("Curvature Result");
			IJ.run("Fire");
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

}
