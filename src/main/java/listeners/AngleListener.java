package listeners;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import distanceTransform.WatershedBinary;
import ellipsoidDetector.Intersectionobject;
import ij.gui.Overlay;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEllipseFit;
import pluginTools.InteractiveEllipseFit.ValueChange;
import utility.Roiobject;

public class AngleListener implements ActionListener {

	final InteractiveEllipseFit parent;

	public AngleListener(final InteractiveEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (parent.automode) {
		
			
			parent.empty = CreateBinary(parent.originalimg, parent.lowprob, parent.highprob);
			
			

			
			
			parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			parent.parentgraphZ =  new 
					HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
			parent.StartComputing();
			
		}
		
		else {
		parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		parent.parentgraphZ =  new 
				HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.StartComputing();
		
		
		}
		
		
		
		

	}

	
	 public RandomAccessibleInterval<BitType> CreateBinary(RandomAccessibleInterval<FloatType> source, double lowprob, double highprob) {
			
			
			RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(source, new BitType());
			
			final RandomAccess<BitType> ranac =  copyoriginal.randomAccess();
			final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();
			
			while(cursor.hasNext()) {
				
				cursor.fwd();
				
				ranac.setPosition(cursor);
				if(cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {
					
					ranac.get().setOne();
				}
				else {
					ranac.get().setZero();
				}
				
				
			}
			
			
			return copyoriginal;
			
		}
}
