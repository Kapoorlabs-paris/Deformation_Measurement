package rectangleListeners;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ij.plugin.frame.RoiManager;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;


/**
 * Updates when mouse is released
 * 
 * @author spreibi
 *
 */
public class RecMouseListener implements MouseListener
{
	final RimSelectionListener parent;
	final InteractiveSimpleEllipseFit grandparent;

	public RecMouseListener( final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent )
	{
		this.parent = parent;
		this.grandparent = grandparent;
	}

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		
		
        parent.standardRectangle = RoiManager.getInstance().getRoi(0).getBounds();
		
		double centerX = parent.offset + parent.standardRectangle.getCenterX();
		double centerY = parent.offsetY + parent.standardRectangle.getCenterY();
		parent.standardRectangle = new Rectangle((int)centerX -parent.height / 2, (int)centerY - parent.width / 2, parent.height,
				parent.width);
		if(grandparent.imp!=null)
			grandparent.imp.setRoi(parent.standardRectangle);
		
	}

	@Override
	public void mousePressed( MouseEvent arg0 ){
		
	
		
	}

	@Override
	public void mouseExited( MouseEvent arg0 ) {}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {}

	@Override
	public void mouseClicked( MouseEvent arg0 ) {}
}
