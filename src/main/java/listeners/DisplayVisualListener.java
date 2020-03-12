package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import curvatureUtils.ParallelResultDisplay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.Binobject;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayVisualListener implements ActionListener {

	
	
	final InteractiveSimpleEllipseFit parent;
	final boolean show;
	
	public  DisplayVisualListener(InteractiveSimpleEllipseFit parent, boolean show) {
	      
		this.parent = parent;
		this.show = show;

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		

		run();

		
		
	}
	
	public  Pair<RandomAccessibleInterval<UnsignedByteType>, RandomAccessibleInterval<FloatType>> run() {
		
		Binobject densesortedMappair = ComputeCurvature.GetZTdenseTrackList(parent);
		parent.sortedMappair = densesortedMappair.sortedmap;

	
		String ID = (String) parent.table.getValueAt(parent.row, 0);
		RandomAccessibleInterval<UnsignedByteType> Blank = ComputeCurvature.MakeDistanceFan(parent, densesortedMappair.sortedmap, ID, show);
		ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> currentresultCurv = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();

		
	for (Pair<String, Pair<Integer, ArrayList<double[]>>> currentCurvature : parent.resultCurvature) {

		if (ID.equals(currentCurvature.getA())) {

			currentresultCurv.add(currentCurvature);

		}

	}
	ParallelResultDisplay display = new ParallelResultDisplay(parent, currentresultCurv, show);
	RandomAccessibleInterval<FloatType> probImg = display.ResultDisplayCircleFit();
	
	return new ValuePair<RandomAccessibleInterval<UnsignedByteType>, RandomAccessibleInterval<FloatType>>(Blank, probImg);
	
	
	}
	
	
	

}
