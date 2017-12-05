package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class DisplayRoiListener implements ActionListener{
	
	
	final InteractiveEllipseFit parent;
	
	
	public DisplayRoiListener (final InteractiveEllipseFit parent) {
		
		this.parent = parent;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		parent.updatePreview(ValueChange.DISPLAYROI);
		
	}
	
	
	
}
