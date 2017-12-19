
package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.ShowView;

public class InsideCutoffListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveEllipseFit parent;
	final float min, max;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public InsideCutoffListener(final InteractiveEllipseFit parent, final Label label, final String string, final float min, final float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.addMouseListener(new EllipseStandardMouseListener(parent, ValueChange.INSIDE));
		deltaScrollbar.setBlockIncrement(1);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		parent.insideCutoff =  (float) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), parent.scrollbarSize, parent.insideCutoffmin, parent.insideCutoffmax);
	
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.scrollbarSize, parent.insideCutoff, parent.insideCutoffmin, parent.insideCutoffmax));

		parent.outsideCutoff = parent.insideCutoff;
		label.setText(string +  " = "  + parent.nf.format(parent.insideCutoff));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	

	
	
	
	
}