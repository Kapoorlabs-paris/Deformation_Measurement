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
		
		
		
		if(parent.curveautomode) {
			
			parent.emptysmooth = utility.Binarization.CreateBinaryBit(parent.originalimgsmooth, parent.lowprob, parent.highprob);
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);

			
			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			parent.parentgraphZ =  new 
					HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartCurvatureComputing();
		}
			
			if(parent.curvesupermode) {

				
				parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
				parent.parentgraphZ =  new 
						HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
				parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
				
				parent.StartCurvatureComputing();
			}
				
				
		
		
		
		
		

	}

	
	
}
