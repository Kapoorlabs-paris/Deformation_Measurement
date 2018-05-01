package curvatureUtils;

import java.awt.Color;
import java.util.ArrayList;

import net.imglib2.util.Pair;
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

		parent.dataset.removeAllSeries();

		parent.dataset.addSeries(utility.ChartMaker.drawPointsInt(currentresultCurvature));
		utility.ChartMaker.setColor(parent.chart, 0, new Color(255, 64, 64));
		utility.ChartMaker.setStroke(parent.chart, 0, 2f);
		parent.updatePreview(ValueChange.CURVERESULT);

	}
	
	
}
