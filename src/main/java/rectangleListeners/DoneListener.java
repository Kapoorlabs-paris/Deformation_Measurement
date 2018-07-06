package rectangleListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class DoneListener implements ActionListener {

	
	final RimSelectionListener parent;
	final InteractiveSimpleEllipseFit grandparent;
	
	public DoneListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent){
		
		this.parent = parent;
		this.grandparent = grandparent;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		parent.Cardframe.dispose();
	
		RoiManager roimanager = RoiManager.getInstance();
		if(roimanager!=null)
			roimanager.dispose();
		if(grandparent.imp!=null)
		{
		
			Roi roi = grandparent.imp.getRoi();
			if(roi.getType() == Roi.RECTANGLE)
				grandparent.imp.deleteRoi();
			
		}
		

}
	
}
