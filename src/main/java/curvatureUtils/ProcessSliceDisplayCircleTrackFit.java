package curvatureUtils;

import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ProcessSliceDisplayCircleTrackFit implements Runnable {

	
	final RandomAccessibleInterval<FloatType> OutputSlice;
    final double minIntensity;
    final double maxIntensity;
    final ArrayList<double[]> TimeCurveList;
	
	public ProcessSliceDisplayCircleTrackFit(final RandomAccessibleInterval<FloatType> OutputSlice,  ArrayList<double[]> TimeCurveList, double minIntensity, double maxIntensity  ) {
		
		
		this.OutputSlice = OutputSlice;
		this.TimeCurveList = TimeCurveList;
		this.minIntensity = minIntensity;
		this.maxIntensity = maxIntensity;
		
	}
	
	
	@Override
	public void run() {
	
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
			AddGaussian.addGaussian(Views.iterable(OutputSlice), Curvature[index], new double[] {X[index], Y[index]}, new double[] {1,1});
			
			

		
			
		}
		final Cursor<FloatType> cursor = Views.iterable(OutputSlice).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

		

				
				double lambda = (cursor.getFloatPosition(0) - OutputSlice.min(0) ) / (OutputSlice.max(0) - OutputSlice.min(0));
				if(cursor.getDoublePosition(1) >= OutputSlice.max(1) - OutputSlice.min(1) - 5)
					cursor.get().setReal( 0 + lambda * (2 * maxIntensity - 0));

			

		}
	}
	
	
	public  double GetMax() {
		
		double MAX = Double.MIN_VALUE;
		
		double[] Curvature = new double[TimeCurveList.size()];
		
		
		
		for (int index = 0; index < TimeCurveList.size(); ++index) {
			
			Curvature[index] = TimeCurveList.get(index)[2];
			
			if(Curvature[index] >= MAX) {
				
				MAX = Curvature[index];
				
			}
			
		}
		
		
		return MAX;
		
	}

}