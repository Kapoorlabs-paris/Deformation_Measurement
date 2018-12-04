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
import pluginTools.Binobject;
import pluginTools.ComputeCurvature;
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
		

		for (Pair<String, double[]> currentperi : parent.resultAngle) {

			if (ID.equals(currentperi.getA())) {

				currentresultPeri.add(currentperi);

			}

		}

	

		if (parent.imp != null) {
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
		}
		Binobject densesortedMappair = ComputeCurvature.GetZTdenseTrackList(parent);
		parent.sortedMappair = densesortedMappair.sortedmap;
		int TimedimensionKymo = parent.AccountedZ.size();

	

		// For dense plot
		HashMap<String, Integer> denseidmap = densesortedMappair.maxid;

		
		int Xkymodimension = denseidmap.get(ID);
	

			long[] size = new long[] { TimedimensionKymo , Xkymodimension + 10 };
			long[] linesize = new long[] {TimedimensionKymo, (long) Math.ceil(parent.insidedistance * 2 + 2)};
			ComputeCurvature.MakeInterKymo(parent, densesortedMappair.sortedmap, size, ID);

			ComputeCurvature.MakeLineKymo(parent, densesortedMappair.sortedmap, linesize, ID);

	}


	
	public static void saveclicked(InteractiveSimpleEllipseFit parent, int trackindex) {

		// Make something happen
		parent.row = trackindex;
		String ID = (String) parent.table.getValueAt(trackindex, 0);
		ArrayList<Pair<String, double[]>> currentresultPeri = new ArrayList<Pair<String, double[]>>();
		

		for (Pair<String, double[]> currentperi : parent.resultAngle) {

			if (ID.equals(currentperi.getA())) {

				currentresultPeri.add(currentperi);

			}

		}

	

		if (parent.imp != null) {
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
		}
		Binobject densesortedMappair = ComputeCurvature.GetZTdenseTrackList(parent);
		parent.sortedMappair = densesortedMappair.sortedmap;
		int TimedimensionKymo = parent.AccountedZ.size();

	

		// For dense plot
		HashMap<String, Integer> denseidmap = densesortedMappair.maxid;

		
		int Xkymodimension = denseidmap.get(ID);
	

			long[] size = new long[] { TimedimensionKymo, Xkymodimension + 1 };
			
			long[] linesize = new long[] {TimedimensionKymo, (long) Math.ceil(parent.insidedistance * 2 + 2)};
			
			
			ComputeCurvature.SaveInterKymo(parent, densesortedMappair.sortedmap, size, ID);

			ComputeCurvature.SaveLineScanKymo(parent, densesortedMappair.sortedmap, linesize, ID);

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
