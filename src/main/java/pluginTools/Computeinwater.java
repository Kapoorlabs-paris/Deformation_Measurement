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
import utility.LabelRansac;
import utility.Roiobject;
import utility.SuperIntersection;
import utility.Watershedobject;

public class Computeinwater {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<BitType> CurrentView;
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	final int t;
	final int z;
	final int maxlabel;
	int percent;

	public Computeinwater(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView,
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

	public Computeinwater(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<BitType> CurrentView,
			final RandomAccessibleInterval<IntType> CurrentViewInt, final int t, final int z, int percent) {

		this.parent = parent;
		this.CurrentView = CurrentView;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.z = z;
		this.maxlabel = 0;
		this.percent = percent;
	}

	public void ParallelRansac() {

		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

		ArrayList<EllipseRoi> resultroi = new ArrayList<EllipseRoi>();
		ArrayList<OvalRoi> resultovalroi = new ArrayList<OvalRoi>();
		ArrayList<Line> resultlineroi = new ArrayList<Line>();
		// Obtain the points of intersections

		ArrayList<Tangentobject> AllPointsofIntersect = new ArrayList<Tangentobject>();
		ArrayList<Intersectionobject> Allintersection = new ArrayList<Intersectionobject>();

		ArrayList<Pair<Ellipsoid, Ellipsoid>> fitmapspecial = new ArrayList<Pair<Ellipsoid, Ellipsoid>>();

		if (parent.supermode) {

			Iterator<Integer> setiter = parent.pixellist.iterator();

			parent.superReducedSamples = new ArrayList<Pair<Ellipsoid, List<Pair<RealLocalizable, BitType>>>>();
			while (setiter.hasNext()) {
				percent++;

				int label = setiter.next();

				Watershedobject current = utility.Watershedobject.CurrentLabelImage(CurrentViewInt, CurrentView, label);

				if (current.Size > parent.minperimeter * parent.minperimeter / 12
						&& current.Size < parent.maxperimeter * parent.maxperimeter / 12) {

					List<Pair<RealLocalizable, BitType>> truths = new ArrayList<Pair<RealLocalizable, BitType>>();
					tasks.add(Executors.callable(new LabelRansac(parent, current.source, truths, t, z, resultroi,
							resultovalroi, resultlineroi, AllPointsofIntersect, Allintersection, fitmapspecial,
							parent.jpb, parent.supermode)));

				}

			}

		}

		else {

			parent.maxlabel = maxlabel;

			for (int label = 1; label <= maxlabel; ++label) {

				percent++;

				Watershedobject current = utility.Watershedobject.CurrentLabelImage(CurrentViewInt, CurrentView, label);

				if (current.Size > parent.minperimeter * parent.minperimeter / 12
						&& current.Size < parent.maxperimeter * parent.maxperimeter / 12) {
					List<Pair<RealLocalizable, BitType>> truths = new ArrayList<Pair<RealLocalizable, BitType>>();
					tasks.add(Executors.callable(new LabelRansac(parent, current.source, truths, t, z, resultroi,
							resultovalroi, resultlineroi, AllPointsofIntersect, Allintersection, fitmapspecial,
							parent.supermode)));
				}
			}
		}
		try {
			taskExecutor.invokeAll(tasks);

			if (parent.supermode) {

				// Get superintersection

				SuperIntersection newintersect = new SuperIntersection(parent);
				AllPointsofIntersect = new ArrayList<Tangentobject>();
				Allintersection = new ArrayList<Intersectionobject>();
				newintersect.Getsuperintersection(resultroi, resultovalroi, resultlineroi, AllPointsofIntersect,
						Allintersection, nThreads, nThreads);

			}
			if (parent.automode) {

				String uniqueID = Integer.toString(z) + Integer.toString(t);
				Roiobject currentobject = new Roiobject(resultroi, resultovalroi, resultlineroi, z, t, true);
				parent.ZTRois.put(uniqueID, currentobject);

				DisplayAuto.Display(parent);
			}

		} catch (InterruptedException e1) {

		}

	}

}
