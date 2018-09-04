package curvatureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.qos.logback.classic.gaffer.GafferConfigurator;
import ij.IJ;
import ij.ImagePlus;
import kalmanForSegments.Segmentobject;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
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
	

	// Create a constructor


	public ParallelResultDisplay(InteractiveSimpleEllipseFit parent, ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv) {

		this.parent = parent;
		this.currentresultCurv = currentresultCurv;
		
		IJ.log("Making curvature image, please wait");
		
	}
	
	
	public void ResultDisplayCircleFit() {


		// Make an image of same size as the original image in N-dimension
		RandomAccessibleInterval<FloatType> probImg = new ArrayImgFactory<FloatType>().create(parent.originalimgbefore,
				new FloatType());

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
				cursor.get().setReal(0 + lambda * (2 * maxCurvature - 0));

		}

		ImagePlus imp = ImageJFunctions.show(probImg);
		imp.setTitle("Curvature RMS Result");
		IJ.run("Fire");

	
		
		
	}
	
	
	
	
	
	
	public void ResultDisplayCircleTrackFit() {
		
		
		
		
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		
        // Make an image of same size as the original image in N-dimension
		RandomAccessibleInterval<FloatType> probImg = new ArrayImgFactory<FloatType>().create(parent.originalimgbefore,
				new FloatType());
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

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
		}

		
	
		
		
		
		
		// Iterate over the time points computation was performed
		while (itZ.hasNext()) {

			int time = itZ.next().getValue();

			ArrayList<double[]> TimeCurveList = parent.HashresultCurvature.get(time);
			
			
			if(TimeCurveList!=null) {
			int listsize = TimeCurveList.size();
			
			
			if (listsize >= parent.KymoDimension) {
				
				parent.KymoDimension = listsize;
				
			}
			
			RandomAccessibleInterval<FloatType> CurrentViewprobImg = utility.Slicer.getCurrentView(probImg, time,
					parent.thirdDimensionSize, 1, parent.fourthDimensionSize);

			
			
			// Add all the tasks to be executed in parallel
			tasks.add(Executors
					.callable(new ProcessSliceDisplayCircleTrackFit(CurrentViewprobImg,  TimeCurveList, minCurvature, maxCurvature)));

		
			
		
		}

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
