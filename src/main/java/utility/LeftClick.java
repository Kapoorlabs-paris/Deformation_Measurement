package utility;

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
		

	int index =	parent.roimanager.getRoiIndex(parent.nearestRoiCurr);
	
		if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()){
			
		
			
			parent.roimanager.select(index);
		}
		
	
		
		
	}
	

}
