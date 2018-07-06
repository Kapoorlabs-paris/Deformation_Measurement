package rectangleListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import ij.plugin.frame.RoiManager;
import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class RectheightListener implements AdjustmentListener {
	final Label label;
	final String string;
	RimSelectionListener parent;
	InteractiveSimpleEllipseFit grandparent;
	final float min;
	final int scrollbarSize;

	float max;
	final JScrollBar deltaScrollbar;

	public RectheightListener(final RimSelectionListener parent, InteractiveSimpleEllipseFit grandparent, final Label label, final String string, final float min, float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
	    this.grandparent = grandparent;
		this.scrollbarSize = scrollbarSize;

		deltaScrollbar.addMouseListener( new RecMouseListener( parent, grandparent ) );
		this.deltaScrollbar = deltaScrollbar;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
	
		
		
		
		max = parent.maxheight;
		parent.height = (int) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);

		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.height, min, max, scrollbarSize));

		label.setText(string +  " = "  + (parent.height) + "      ");
		if(e.getValueIsAdjusting())
		parent.heightField.setText(Integer.toString(parent.height));
		
		parent.IntensityRegion.validate();
		parent.IntensityRegion.repaint();
	
		
	}
	
	
}
