package curvatureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccess;
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
					.callable(new ProcessSliceDisplay(CurrentViewprobImg,  TimeCurveList, minCurvature, maxCurvature)));

		}

		}
		try {
			taskExecutor.invokeAll(tasks);

			ImagePlus imp = ImageJFunctions.show(probImg);
			imp.setTitle("Curvature Result");
			IJ.run("Fire");
			
			// Make Kymographs
			long[] size = new long[] { (int)parent.AccountedZ.size(),parent.KymoDimension };
			
			MakeKymo(size);
			
			
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
		
		

	}
	
	
	public void MakeKymo(long[] size) {
		
		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,  new FloatType());  
	
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
		
		RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();
		// Iterate over the time points computation was performed
		while (itZ.hasNext()) {

			int time = itZ.next().getValue();

			ArrayList<double[]> TimeCurveList = parent.HashresultCurvature.get(time);
			if(TimeCurveList!=null) {
			int count = 0;
			double[] X = new double[TimeCurveList.size()];
			double[] Y = new double[TimeCurveList.size()];
			double[] Curvature = new double[TimeCurveList.size()];
			double[] Intensity = new double[TimeCurveList.size()];
			double[] IntensitySec = new double[TimeCurveList.size()];
			
			
			for (int index = 0; index < TimeCurveList.size(); ++index) {
				
				
				X[index] = TimeCurveList.get(index)[0];
				Y[index] = TimeCurveList.get(index)[1];
				Curvature[index] = TimeCurveList.get(index)[2];
				Intensity[index] = TimeCurveList.get(index)[3];
				IntensitySec[index] = TimeCurveList.get(index)[4];
			ranac.setPosition(time, 0);
			ranac.setPosition(count, 1);
			ranac.get().setReal(Curvature[index]);
			count++;
		}
		
			}
	}
		
		ImageJFunctions.show(CurvatureKymo).setTitle("Curvature Kymo");
	}
	

}
