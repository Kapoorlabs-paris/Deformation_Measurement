package listeners;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.gui.Overlay;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.Roiobject;

public class AngleListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public AngleListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// For computing angles
		parent.superReducedSamples.clear();
		parent.table.removeAll();
		parent.table.repaint();
		parent.Tracklist.clear();
		parent.overlay.clear();
		parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		if (parent.supermode) {
		
			
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
			
			

			
			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			parent.parentgraphZ =  new 
					HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartComputing();
			
		}
		
       if (parent.automode) {
		
			
			parent.emptysmooth = utility.Binarization.CreateBinaryBit(parent.originalimgsmooth, parent.lowprob, parent.highprob);
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);

			
			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			parent.parentgraphZ =  new 
					HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartComputing();
			
		}
		
		
		else if(!parent.automode && !parent.supermode) {
		
		parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		parent.parentgraphZ =  new 
				HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.StartComputing();
		System.out.println("Starting computing manual mode");
		
		}
		
		
		
		

	}

	
	
}
