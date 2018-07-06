package rectangleListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import listeners.RimSelectionListener;
import pluginTools.InteractiveSimpleEllipseFit;

public class RectoffsetYListener implements AdjustmentListener {
	final Label label;
	final String string;
	RimSelectionListener parent;
	final float min;
	final int scrollbarSize;
    InteractiveSimpleEllipseFit grandparent;
	float max;
	final JScrollBar deltaScrollbar;

	public RectoffsetYListener(final RimSelectionListener parent, final InteractiveSimpleEllipseFit grandparent,  final Label label, final String string, final float min, float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.scrollbarSize = scrollbarSize;
        this.grandparent = grandparent;
		deltaScrollbar.addMouseListener( new RecMouseListener( parent, grandparent ) );
		this.deltaScrollbar = deltaScrollbar;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		max = parent.maxoffsetY;
		
		parent.offsetY = (int) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);

		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.offsetY, min, max, scrollbarSize));

		label.setText(string +  " = "  + (parent.offsetY) + "      ");
		if(e.getValueIsAdjusting())
		parent.offsetYField.setText(Integer.toString(parent.offsetY));
		parent.IntensityRegion.validate();
		parent.IntensityRegion.repaint();
	
		
	}
	
	
}
