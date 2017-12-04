package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;


public class EllipseNonStandardMouseListener implements MouseMotionListener
{
	final InteractiveEllipseFit parent;
	final ValueChange change;

	public EllipseNonStandardMouseListener( final InteractiveEllipseFit parent, final ValueChange change )
	{
		this.parent = parent;
		this.change = change;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
  
		

		parent.updatePreview(change);
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}
	
}
