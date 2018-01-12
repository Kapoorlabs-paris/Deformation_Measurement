package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;

public class RedoListener implements ActionListener {

	final InteractiveEllipseFit parent;

	public RedoListener(final InteractiveEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.updatePreview(ValueChange.RectRoi);
		parent.StartComputingCurrent();
		
		
		
		

	}

}
