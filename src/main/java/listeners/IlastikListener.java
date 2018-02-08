package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveEllipseFit;

public class IlastikListener implements ItemListener {
	
	final InteractiveEllipseFit parent;
	
	public IlastikListener(final InteractiveEllipseFit parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
	        
			parent.automode = false;
			parent.ChooseMethod.setEnabled(true);
			parent.ChooseColor.setEnabled(true);
			parent.Roibutton.setEnabled(true);
		}
		
        else if (arg0.getStateChange() == ItemEvent.SELECTED) {
		
		parent.automode = true;
		
		parent.ChooseMethod.setEnabled(false);
		parent.ChooseColor.setEnabled(false);
		parent.Roibutton.setEnabled(false);
		
	}
	

}

}