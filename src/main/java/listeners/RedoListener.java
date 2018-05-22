package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.ImageStack;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class RedoListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public RedoListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.superReducedSamples.clear();
if (parent.supermode) {
		
			
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
			
			

			
			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			parent.parentgraphZ =  new 
					HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartComputingCurrent();
			
		}
		
       if (parent.automode) {
		
			
			parent.emptysmooth = utility.Binarization.CreateBinaryBit(parent.originalimgsmooth, parent.lowprob, parent.highprob);
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);

			
			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			parent.parentgraphZ =  new 
					HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartComputingCurrent();
			
		}
		
		
		else if(!parent.automode && !parent.supermode) {
		parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		parent.parentgraphZ =  new 
				HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.StartComputingCurrent();
		
		
		}
		
		
		
		
		

	}

}
