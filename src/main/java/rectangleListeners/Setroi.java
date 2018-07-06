package rectangleListeners;

import java.awt.Rectangle;

import ij.plugin.frame.RoiManager;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class Setroi {

	
	
	public static void Setup(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent) {
		
		
		
		// Some value is remembered
		  parent.standardRectangle = RoiManager.getInstance().getRoi(0).getBounds();
			
			double centerX = parent.offset + parent.standardRectangle.getCenterX();
			double centerY = parent.offsetY + parent.standardRectangle.getCenterY();
			parent.standardRectangle = new Rectangle((int)centerX -parent.height / 2, (int)centerY - parent.width / 2, parent.height,
					parent.width);
			if(grandparent.imp!=null)
				grandparent.imp.setRoi(parent.standardRectangle);
	}
}
