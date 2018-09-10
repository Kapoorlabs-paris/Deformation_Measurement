package listeners;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import comboSliderTextbox.SliderBoxGUI;
import pluginTools.InteractiveSimpleEllipseFit;

public class RunCelltrackCirclemodeListener implements ItemListener {

	InteractiveSimpleEllipseFit parent;

	public RunCelltrackCirclemodeListener(InteractiveSimpleEllipseFit parent) {
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {

			parent.pixelcelltrackcirclefits = true;
			parent.distancemethod = false;
			

		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.pixelcelltrackcirclefits = false;
			parent.distancemethod = true;
			parent.resolution = Integer.parseInt(parent.resolutionField.getText());
		}

	}

}