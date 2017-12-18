package utility;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import ellipsoidDetector.Distance;
import ij.gui.OvalRoi;
import ij.plugin.frame.RoiManager;
import mpicbg.imglib.util.Util;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class LeftClick {
	
	
public static void LeftRightClick(int x, int y, MouseEvent e, InteractiveEllipseFit parent){
		

	
	
	
	if(SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown() && e.isAltDown()){
		
		

		int index =	parent.roimanager.getRoiIndex(parent.nearestRoiCurr);
		parent.roimanager.select(index);
		parent.nearestRoiCurr.setStrokeColor(parent.colorChange);
		parent.nearestRoiCurr.setStrokeWidth(2);
		
		parent.imp.updateAndDraw();
		
		parent.updatePreview(ValueChange.DISPLAYROI);
		
		
		
		

	}
	

	
	
	if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()  ){
		System.out.println("pressed");
		if (!parent.jFreeChartFrame.isVisible())
			parent.jFreeChartFrame = utility.ChartMaker.display(parent.chart, new Dimension(500, 500));
		
		parent.displayclicked(parent.rowchoice);
	}
		
		
	
		
		
	}
	

}
