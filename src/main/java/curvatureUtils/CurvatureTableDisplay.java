package curvatureUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.jfree.data.contour.DefaultContourDataset;

import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.process.LUT;
import net.imagej.display.ColorTables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ColorTable8;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ChartMaker;
import utility.Curvatureobject;

public class CurvatureTableDisplay {

	public static void displayclicked(InteractiveSimpleEllipseFit parent, int trackindex) {

		
		// Make something happen
		parent.row = trackindex;
		String ID = (String) parent.table.getValueAt(trackindex, 0);
		ArrayList<Pair<String, double[]>> currentresultPeri = new ArrayList<Pair<String, double[]>>();
		ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();

		for (Pair<String, double[]> currentperi : parent.resultAngle) {

			if (ID.equals(currentperi.getA())) {

				currentresultPeri.add(currentperi);

			}

		}

		for (Pair<String, Pair<Integer, ArrayList<double[]>>> currentCurvature : parent.resultCurvature) {

			if (ID.equals(currentCurvature.getA())) {

				currentresultCurv.add(currentCurvature);

			}

		}

		if (parent.imp != null) {
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
		}

		parent.contdataset.removeAllSeries();
		parent.contdataset.addSeries(ChartMaker.drawCurvePoints(currentresultPeri));

		parent.chart = utility.ChartMaker.makeChart(parent.contdataset, "Perimeter Evolution", "Time", "Perimeter");

		
		ParallelResultDisplay display = new ParallelResultDisplay(parent, currentresultCurv);
		display.ResultDisplay();
		
	

		parent.jFreeChartFrame.dispose();
		parent.jFreeChartFrame.repaint();

	}
	
public static void displayclickedSegment(InteractiveSimpleEllipseFit parent, int trackindex) {

		
		// Make something happen
		parent.row = trackindex;
		String ID = (String) parent.table.getValueAt(trackindex, 0);
		ArrayList<Pair<String, double[]>> currentresultPeri = new ArrayList<Pair<String, double[]>>();
		ArrayList<Pair<String, Pair<Integer, Double>>> currentresultCurv = new ArrayList<Pair<String, Pair<Integer, Double>>>();

	

		for (Pair<String, Pair<Integer, Double>> currentCurvature : parent.resultSegCurvature) {

			
			if (ID.equals(currentCurvature.getA())) {

				currentresultCurv.add(currentCurvature);

			}

		}

		if (parent.imp != null) {
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
		}

		parent.contdataset.removeAllSeries();
		parent.contdataset.addSeries(ChartMaker.drawCurveSegPoints(currentresultCurv));

		parent.chart = utility.ChartMaker.makeChart(parent.contdataset, "Segment Curvature Evolution", "Time", "Curvature");

		
	
		
	

		parent.jFreeChartFrame.dispose();
		parent.jFreeChartFrame.repaint();

	}
	

	public static Pair<Double, Double> RangePlot(ArrayList<Curvatureobject> currentresultCurvature, int index) {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (Curvatureobject currentcurvature : currentresultCurvature) {

			if (currentcurvature.cord[index] < min)
				min = currentcurvature.cord[index];
			if (currentcurvature.cord[index] > max)
				max = currentcurvature.cord[index];

		}

		return new ValuePair<Double, Double>(min, max);

	}

}
