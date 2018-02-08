package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class LowProbListener implements AdjustmentListener {
	
	final Label label;
	final String string;
	InteractiveEllipseFit parent;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar deltaScrollbar;
	
	public LowProbListener(final InteractiveEllipseFit parent, final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar deltaScrollbar) {
		
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
		this.deltaScrollbar = deltaScrollbar;
		
		
		
		deltaScrollbar.addMouseMotionListener(new EllipseNonStandardMouseListener(parent, ValueChange.SEG));
		deltaScrollbar.addMouseListener(new EllipseStandardMouseListener(parent, ValueChange.SEG));
		
		
		
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		parent.lowprob = utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
		deltaScrollbar
		.setValue(utility.Slicer.computeScrollbarPositionFromValue( parent.lowprob, min, max, scrollbarSize));
		label.setText(string +  " = "  + parent.lowprob);
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		
	}
	
	
	
	

}
