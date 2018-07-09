package rectangleListeners;

import java.awt.Rectangle;

import ij.ImagePlus;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class SetRectangle {

	
	final RimSelectionListener parent;
	final InteractiveSimpleEllipseFit grandparent;

	public SetRectangle( final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent )
	{
		this.parent = parent;
		this.grandparent = grandparent;
	}
	
	public  void setRect() {
		
		
		double centerX = parent.offset + parent.standardRectangle.getCenterX();
		double centerY = parent.offsetY + parent.standardRectangle.getCenterY();
	
		
		
		parent.standardRectangle = new Rectangle((int)centerX - parent.height / 2, (int)centerY - parent.width / 2, parent.height,
				parent.width);
		if(grandparent.imp==null)
			grandparent.imp = new ImagePlus();
			
			grandparent.imp.setRoi(parent.standardRectangle);
			
			grandparent.imp.updateAndDraw();
			
			
			
			
	}
	
}
