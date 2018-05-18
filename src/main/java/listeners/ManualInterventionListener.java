package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class ManualInterventionListener implements ActionListener {
	
	final InteractiveSimpleEllipseFit parent;

	public ManualInterventionListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		parent.StartManualIntervention();
		
	}

}
