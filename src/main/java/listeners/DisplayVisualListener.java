package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import curvatureUtils.ParallelResultDisplay;
import net.imglib2.util.Pair;
import pluginTools.Binobject;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayVisualListener implements ActionListener {

	
	
	final InteractiveSimpleEllipseFit parent;
	
	public  DisplayVisualListener(InteractiveSimpleEllipseFit parent) {
	      
		this.parent = parent;

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		

		Binobject densesortedMappair = ComputeCurvature.GetZTdenseTrackList(parent);
		parent.sortedMappair = densesortedMappair.sortedmap;

	
		String ID = (String) parent.table.getValueAt(parent.row, 0);
		ComputeCurvature.MakeDistanceFan(parent, densesortedMappair.sortedmap, ID);
		ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();

		
	for (Pair<String, Pair<Integer, ArrayList<double[]>>> currentCurvature : parent.resultCurvature) {

		if (ID.equals(currentCurvature.getA())) {

			currentresultCurv.add(currentCurvature);

		}

	}
	ParallelResultDisplay display = new ParallelResultDisplay(parent, currentresultCurv);
	display.ResultDisplayCircleFit();

		
		
	}
	
	
	
	

}
