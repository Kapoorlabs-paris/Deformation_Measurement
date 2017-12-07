package utility;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import ij.gui.OvalRoi;
import ij.plugin.frame.RoiManager;
import mpicbg.imglib.util.Util;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class LeftClick {
	
	
public static void LeftRightClick(int x, int y, MouseEvent e, InteractiveEllipseFit parent){
		

	
	
	
	if(SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown() ){
		
		

		int index =	parent.roimanager.getRoiIndex(parent.nearestRoiCurr);
		parent.roimanager.select(index);
		parent.nearestRoiCurr.setStrokeColor(parent.colorChange);
	
		
		parent.imp.updateAndDraw();
		
		parent.updatePreview(ValueChange.DISPLAYROI);
	}
	
	
	if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() ){
		
		parent.updatePreview(ValueChange.ROI);
		
	}
		
		
	
		
		
	}
	

}
