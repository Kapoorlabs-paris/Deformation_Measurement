package listeners;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.IlastikEllipseFileChooser;
import pluginTools.InteractiveSimpleEllipseFit;

public class SupermodeListener implements ItemListener {

	
	public final IlastikEllipseFileChooser parent;
	
	public SupermodeListener(final IlastikEllipseFileChooser parent) {
		
		
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if(e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.superpixel = false;
			parent.simple = true;
			
		}
		
		else if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.superpixel = true;
			parent.simple = false;
			
			
			parent.Panelsuperfile.setEnabled(true);
			parent.ChoosesuperImage.setEnabled(true);
		
			
			
		}
		
		
		
		
	}
	
	
	
	
}
