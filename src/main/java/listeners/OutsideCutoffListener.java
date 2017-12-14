package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class OutsideCutoffListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveEllipseFit parent;
	final float min, max;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public OutsideCutoffListener(final InteractiveEllipseFit parent, final Label label, final String string, final float min, final float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.addMouseListener(new EllipseStandardMouseListener(parent, ValueChange.OUTSIDE));
		deltaScrollbar.setBlockIncrement(1);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		parent.outsideCutoff = parent.insideCutoff;  
				//utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
	
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.outsideCutoff, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.nf.format(parent.outsideCutoff));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	

	
	
	
	
}