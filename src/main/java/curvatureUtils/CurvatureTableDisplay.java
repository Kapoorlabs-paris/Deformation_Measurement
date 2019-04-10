package curvatureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.Binobject;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;

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
			long[] linesize = new long[] {TimedimensionKymo, (long) Math.ceil(parent.minNumInliers * (parent.insidedistance * 2 +10)) };
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
			
			long[] linesize = new long[] {TimedimensionKymo, (long) Math.ceil(parent.minNumInliers * (parent.insidedistance * 2 + 2))};
			
			
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
