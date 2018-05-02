package curvatureUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import org.jfree.data.contour.DefaultContourDataset;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class CurvatureTableDisplay {

	
	
	
	public static void displayclicked(InteractiveSimpleEllipseFit parent, int trackindex) {

		// Make something happen
		parent.row = trackindex;
		Integer ID = (Integer) parent.table.getValueAt(trackindex, 0);
		ArrayList<Pair<Integer, double[]>> currentresultCurvature = new ArrayList<Pair<Integer, double[]>>();
	
		for (Pair<Integer, double[]> currentcurvature : parent.resultCurvature) {

			if (ID.equals(currentcurvature.getA())) {

				currentresultCurvature.add(currentcurvature);

			}

		}

		Double[] X = new Double[currentresultCurvature.size()];
		Double[] Y = new Double[currentresultCurvature.size()];
		Double[] Z = new Double[currentresultCurvature.size()];
		
		for (int index = 0; index < currentresultCurvature.size(); ++index) {
			
			X[index] = currentresultCurvature.get(index).getB()[0];
			Y[index] = currentresultCurvature.get(index).getB()[1];
			Z[index] = currentresultCurvature.get(index).getB()[2];
			
		}
		
		
		

		
		
		Pair<Double, Double> minmaxX = RangePlot(currentresultCurvature, 0);
		Pair<Double, Double> minmaxY = RangePlot(currentresultCurvature, 1);
	
		parent.contdataset.initialize(X, Y, Z);
		
		parent.chart = utility.ChartMaker.makeContourChart(parent.contdataset, "Curvature Measurment", minmaxX.getA() - 5, minmaxX.getB() + 5, minmaxY.getA() - 5, minmaxY.getB() + 5 );
		parent.jFreeChartFrame.dispose();
		parent.jFreeChartFrame.repaint();
		
		
		
		parent.updatePreview(ValueChange.CURVERESULT);

	}
	
	
	public static Pair<Double, Double> RangePlot(ArrayList<Pair<Integer, double[]>> currentresultCurvature, int index){
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for(Pair<Integer, double[]> currentcurvature: currentresultCurvature) {
			
			if (currentcurvature.getB()[index] < min)
				min = currentcurvature.getB()[index];
			if(currentcurvature.getB()[index] > max)
				max = currentcurvature.getB()[index];
			
		}
		
		return new ValuePair<Double, Double> (min, max);
		
		
		
	}
	
	
}
