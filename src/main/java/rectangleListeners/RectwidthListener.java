package rectangleListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class RectwidthListener implements AdjustmentListener {
	final Label label;
	final String string;
	RimSelectionListener parent;
	InteractiveSimpleEllipseFit grandparent;
	final float min;
	final int scrollbarSize;

	float max;
	final JScrollBar deltaScrollbar;

	public RectwidthListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent,  final Label label, final String string, final float min, float max,
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
		
		max = parent.maxwidth;
		
		parent.width = (int) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);

		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.width, min, max, scrollbarSize));

		label.setText(string +  " = "  + (parent.width) + "      ");
		if(e.getValueIsAdjusting())
		parent.widthField.setText(Integer.toString(parent.width));
		parent.IntensityRegion.validate();
		parent.IntensityRegion.repaint();
	
		
	}
	
	
}
