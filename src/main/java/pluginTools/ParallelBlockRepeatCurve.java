package pluginTools;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JProgressBar;

import curvatureUtils.ExpandBorder;
import ellipsoidDetector.Intersectionobject;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class ParallelBlockRepeatCurve  implements Runnable {

	
	public final InteractiveSimpleEllipseFit parent;
	
	public final int z;
	
	public final int t;
	
	public int percent;
	
	public final JProgressBar jpb;
	
	public ParallelBlockRepeatCurve(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb, final int z, final int t, final int percent) {
		
		this.parent = parent;
		
		this.z = z;
		
		this.t = t;
		
		this.percent = percent;
		
		this.jpb = jpb;
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
	    compute.ParallelRansacCurve();
	
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
