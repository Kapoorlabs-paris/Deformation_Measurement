package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.ransac.RansacModels.Ellipsoid;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.DisplayAuto;
import utility.LabelFit;
import utility.LabelRansac;
import utility.NormalIntersection;
import utility.Roiobject;
import utility.SuperIntersection;
import utility.Watershedobject;

public class Computeincurvature {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> CurrentView;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final int t;
	final int z;
	final int maxlabel;
	int percent;

	// In curvature
	public Computeincurvature(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView,
			final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z, int percent,
			final int maxlabel) {

		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.maxlabel = maxlabel;
		this.percent = percent;
	}

	public Computeincurvature(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView,
			final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z, int percent) {

		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.maxlabel = 0;
		this.percent = percent;
	}

	public void ParallelFit() {

		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		ArrayList<EllipseRoi> resultroi = new ArrayList<EllipseRoi>();
	

		ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial = new ArrayList<Pair<Ellipsoid, Ellipsoid>>();

		if (parent.curveautomode || parent.curvesupermode) {

			Iterator<Integer> setiter = parent.pixellist.iterator();

			parent.superReducedSamples = new ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, BitType>>>>();
			while (setiter.hasNext()) {
				percent++;

				int label = setiter.next();
				
				Watershedobject current = utility.Watershedobject.CurrentLabelImage(CurrentViewInt, CurrentView, label);
				
				// Neglect the small watershed regions by choosing only those regions which have more than 9 candidate points for ellipse fitting
				//System.out.println(current.meanIntensity + " " + current.Size + "Check size");
				if (current.meanIntensity > parent.minellipsepoints) {
					
					List<Pair<RealLocalizable, BitType>> truths = new ArrayList<Pair<RealLocalizable, BitType>>();
					
					
					tasks.add(Executors.callable(new LabelFit(parent, current.source, truths, t, z, resultroi,fitmapspecial,
							parent.jpb)));

				}

			}

		}


		
		try {
			taskExecutor.invokeAll(tasks);

// Change this method
				// Get superintersection
/*
				
				SuperIntersection newintersect = new SuperIntersection(parent);
				AllPointsofIntersect = new ArrayList<Tangentobject>();
				Allintersection = new ArrayList<Intersectionobject>();
				newintersect.Getsuperintersection(resultroi, resultovalroi, resultlineroi, AllPointsofIntersect,
						Allintersection, t, z);
			

*/			
		
		} catch (InterruptedException e1) {

		}

	}

}
