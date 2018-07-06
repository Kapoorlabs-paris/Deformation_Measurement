package rectangleListeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import listeners.RimSelectionListener;


/**
 * Updates when mouse is released
 * 
 * @author spreibi
 *
 */
public class RecMouseListener implements MouseListener
{
	final RimSelectionListener parent;

	public RecMouseListener( final RimSelectionListener parent )
	{
		this.parent = parent;
	}

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		
	}

	@Override
	public void mousePressed( MouseEvent arg0 ){}

	@Override
	public void mouseExited( MouseEvent arg0 ) {}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {}

	@Override
	public void mouseClicked( MouseEvent arg0 ) {}
}
