package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveSimpleEllipseFit;

public class CurvatureListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public CurvatureListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	// For curvatrue
	@Override
	public void actionPerformed(ActionEvent e) {

		parent.table.removeAll();
		parent.table.repaint();
		parent.localCurvature.clear();
		parent.AlllocalCurvature.clear();
		parent.overlay.clear();
		parent.Tracklist.clear();
		parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		
		
		parent.displayCircle.setState(false);
		parent.displaySegments.setState(false);
		parent.displayIntermediate = false;
		parent.displayIntermediateBox = false;
		if (parent.curveautomode) {

			parent.emptysmooth = utility.Binarization.CreateBinaryBit(parent.originalimgsmooth, parent.lowprob,
					parent.highprob);
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);

			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);
			parent.parentgraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartCurvatureComputing();
			
			
		}

		if (parent.curvesupermode) {

			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);
			parent.parentgraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);

			parent.StartCurvatureComputing();
		}

	}

}
