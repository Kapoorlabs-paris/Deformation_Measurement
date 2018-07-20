package curvatureUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.jfree.data.contour.DefaultContourDataset;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.process.LUT;
import kalmanForSegments.Segmentobject;
import net.imagej.display.ColorTables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
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
import utility.Listordereing;

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

	

		if (parent.imp != null) {
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
		}

		parent.contdataset.removeAllSeries();
		parent.contdataset.addSeries(ChartMaker.drawCurvePoints(currentresultPeri));

		parent.chart = utility.ChartMaker.makeChart(parent.contdataset, "Perimeter Evolution", "Time", "Perimeter");
		
		for (Pair<String, Pair<Integer, ArrayList<double[]>>> currentCurvature : parent.resultCurvature) {

			if (ID.equals(currentCurvature.getA())) {

				currentresultCurv.add(currentCurvature);

			}

		}
		ParallelResultDisplay display = new ParallelResultDisplay(parent, currentresultCurv);
		display.ResultDisplayCircleTrackFit();

		parent.jFreeChartFrame.dispose();
		parent.jFreeChartFrame.repaint();

	}

	public static void displayclickedSegment(InteractiveSimpleEllipseFit parent, int trackindex) {

		// Make something happen
		parent.row = trackindex;
		String ID = (String) parent.table.getValueAt(trackindex, 0);
		ArrayList<Pair<String, double[]>> currentresultPeri = new ArrayList<Pair<String, double[]>>();
		ArrayList<Pair<String, Pair<Integer, Double>>> currentresultCurv = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		ArrayList<Pair<String, Pair<Integer, Double>>> currentresultIntensityA = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		ArrayList<Pair<String, Pair<Integer, Double>>> currentresultIntensityB = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		ArrayList<Pair<String, Pair<Integer, Double>>> currentresultPerimeter = new ArrayList<Pair<String, Pair<Integer, Double>>>();
		
		
		ArrayList<Pair<String, Pair<Integer, List<RealLocalizable>>>> SubcurrentresultCurv = 
				new ArrayList<Pair<String, Pair<Integer, List<RealLocalizable>>>>();
		
		
		for (Pair<String, Pair<Integer, List<RealLocalizable>>> currentCurvature : parent.SubresultCurvature) {

			if (ID.equals(currentCurvature.getA())) {

				SubcurrentresultCurv.add(currentCurvature);

			}

		}
		
		for (Pair<String, Pair<Integer, Double>> currentCurvature : parent.resultSegCurvature) {

			if (ID.equals(currentCurvature.getA())) {
				currentresultCurv.add(currentCurvature);
             
			}

		}
		
		
		for (Pair<String, Pair<Integer, Double>> currentCurvature : parent.resultSegIntensityA) {

			if (ID.equals(currentCurvature.getA())) {
				currentresultIntensityA.add(currentCurvature);
             
			}

		}
		
		for (Pair<String, Pair<Integer, Double>> currentCurvature : parent.resultSegIntensityB) {

			if (ID.equals(currentCurvature.getA())) {
				currentresultIntensityB.add(currentCurvature);
             
			}

		}
		
		for (Pair<String, Pair<Integer, Double>> currentCurvature : parent.resultSegPerimeter) {

			if (ID.equals(currentCurvature.getA())) {
				currentresultPerimeter.add(currentCurvature);
             
			}

		}
		

		if (parent.imp != null) {
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
		}

		parent.contdataset.removeAllSeries();
		parent.contdataset.addSeries(ChartMaker.drawCurveSegPoints(currentresultCurv));

		parent.chart = utility.ChartMaker.makeChart(parent.contdataset, "Segment Curvature Evolution", "Time",
				"Curvature");

		parent.jFreeChartFrame.dispose();
		parent.jFreeChartFrame.repaint();
		
		parent.IntensityAdataset.removeAllSeries();
		parent.IntensityAdataset.addSeries(ChartMaker.drawCurveSegPoints(currentresultIntensityA));

		parent.chartIntensityA = utility.ChartMaker.makeChart(parent.IntensityAdataset, "Segment Intensity Evolution", "Time",
				"IntensityA");

		parent.jFreeChartFrameIntensityA.dispose();
		parent.jFreeChartFrameIntensityA.repaint();
		
		
		parent.IntensityBdataset.removeAllSeries();
		parent.IntensityBdataset.addSeries(ChartMaker.drawCurveSegPoints(currentresultIntensityB));

		parent.chartIntensityB = utility.ChartMaker.makeChart(parent.IntensityBdataset, "Segment Intensity Evolution", "Time",
				"IntensityB");

		parent.jFreeChartFrameIntensityB.dispose();
		parent.jFreeChartFrameIntensityB.repaint();
		
		
		parent.Perimeterdataset.removeAllSeries();
		parent.Perimeterdataset.addSeries(ChartMaker.drawCurveSegPoints(currentresultPerimeter));

		parent.chartPerimeter = utility.ChartMaker.makeChart(parent.Perimeterdataset, "Segment Perimeter Evolution", "Time",
				"Perimeter");

		parent.jFreeChartFramePerimeter.dispose();
		parent.jFreeChartFramePerimeter.repaint();
		
		ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> AllcurrentresultCurv = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();
		
		for (Pair<String, Pair<Integer, ArrayList<double[]>>> currentCurvature : parent.resultCurvature) {

			if (ID.equals(currentCurvature.getA())) {

				AllcurrentresultCurv.add(currentCurvature);

			}

		}
		
		
		ParallelResultDisplay display = new ParallelResultDisplay(parent, AllcurrentresultCurv);
		display.ResultDisplayCircleFit();

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
