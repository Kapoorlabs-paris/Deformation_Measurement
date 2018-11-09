package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import curvatureUtils.ExpandBorder;
import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import ij.ImageStack;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import track.TrackingFunctions;

public class ParallelBlockRepeatCurve  implements Runnable {

	
	public final InteractiveSimpleEllipseFit parent;
	
	public final ArrayList<HashMap<String, ArrayList<Intersectionobject>>> Alldensemap;
	
	public final int z;
	
	public final int t;
	
	public int percent;
	
	public final JProgressBar jpb;
	
	public ParallelBlockRepeatCurve(final InteractiveSimpleEllipseFit parent, final ArrayList<HashMap<String, ArrayList<Intersectionobject>>> Alldensemap,  final JProgressBar jpb, final int z, final int t, final int percent) {
		
		this.parent = parent;
		
		this.z = z;
		
		this.t = t;
		
		this.percent = percent;
		
		this.jpb = jpb;
		
		this.Alldensemap = Alldensemap;
	}
	
	
	@Override
	public void run() {
		


		
		parent.updatePreview(ValueChange.THIRDDIMmouse);
		percent++;
		if(jpb!=null )
		utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize + 1),
				"Computing Curvature = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
						+ parent.thirdDimensionSize);

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.originalimgsuper, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(CurrentViewInt), min, max);
		// Neglect the background class label
        int currentLabel = min.get();
	
           RandomAccessibleInterval<IntType> expanededtotalimg = ExpandBorder.extendBorder(parent, CurrentViewInt, currentLabel);

		//RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
		GetPixelList(expanededtotalimg);
		
		Computeinwater compute = new Computeinwater(parent, CurrentView, expanededtotalimg, t, z, (int) percent);
		HashMap<String, ArrayList<Intersectionobject>> densemap = compute.DualParallelRansacCurve();
	
		Alldensemap.add(densemap);
	}
	
	public static void done(InteractiveSimpleEllipseFit parent) {
		
		

		
		parent.CurrentCurvaturebutton.setEnabled(true);
		parent.Curvaturebutton.setEnabled(true);
        parent.timeslider.setEnabled(true);
        parent.inputFieldT.setEnabled(true);
        parent.distancemode.setEnabled(true);
        parent.Pixelcelltrackcirclemode.setEnabled(true);
        parent.resolutionField.setEnabled(true);
        parent.interiorfield.setEnabled(true);
        parent.Displaybutton.setEnabled(true);
        parent.minInlierslider.setEnabled(true);
        parent.minInlierField.setEnabled(true);
		
		
		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		parent.resultDraw.clear();
		parent.Tracklist.clear();
		parent.denseTracklist.clear();

		parent.SegmentTracklist.clear();
		parent.table.removeAll();

		IJ.log("\n " + "Calculation is Complete or was interupted "  + "\n "
		        + "IF RUNNING IN BATCH MODE, Results are automatically saved in Results folder"
		        + "IF IT WAS NOT A KEYBOARD INTERUPT: " + "\n "
				+ "do a Shift + Left click near the Cell of your choice to display " + "\n "
				+ "Kymographs for Curvature, Intensity " + " \n"
				+ "RMS value which moves with the time slider to fit on the current view of the cell " + " \n"
				+ "Curvature value display as lines connecting the center to the boundary of the cell over time");

		TrackingFunctions track = new TrackingFunctions(parent);
		if (parent.ndims > 3) {

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

			while (itZ.hasNext()) {

				int z = itZ.next().getValue();

			
				SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

				parent.parentgraphZ.put(Integer.toString(z), simplegraph);

				ComputeCurvature.CurvedLineage(parent);

			}

		}

		else {

			SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simpledensegraph = track.Trackdensefunction();

			parent.parentdensegraphZ.put(Integer.toString(1), simpledensegraph);

			ComputeCurvature.CurveddenseLineage(parent);

		}

		
		
	}

	public  void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		int currentLabel = max.get();
		parent.pixellist.clear();
		
		
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();
			if (i != currentLabel ) {

				parent.pixellist.add(i);

				currentLabel = i;

			}

		}

	}
	
	public <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}
	
	
	
}
