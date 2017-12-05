package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class RoiListener implements ActionListener{
	
	
	final InteractiveEllipseFit parent;
	
	
	public RoiListener (final InteractiveEllipseFit parent) {
		
		this.parent = parent;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		parent.updatePreview(ValueChange.ROI);
		
	}

}
